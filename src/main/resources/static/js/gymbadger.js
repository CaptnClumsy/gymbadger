  var PARKS_ONLY_OPTION = 9999;
  var SELECT_ALL_OPTION = 9998;

  var map;
  var gymData;
  var selectedAreaIds = [];
  var parksOnly = false;
  var currentInfoWindow = null;
  var currentMarker = null;
  var currentProps = null;
  var reportTable = null;
  var raidBossData = null;
  var leadersTable = null;
  var totalTable = null;

  function initPage() {
	// Load snazzy info window script
	var script = document.createElement('script');
	script.type = 'text/javascript';
	script.src = 'js/snazzy-info-window-hacked.js';
	document.body.appendChild(script);
	// Setup CSRF token
	$.ajaxSetup({
	  beforeSend : function(xhr, settings) {
	    if (settings.type == 'POST' || settings.type == 'PUT'
		  || settings.type == 'DELETE') {
		  if (!(/^http:.*/.test(settings.url) || /^https:.*/
		    .test(settings.url))) {
		      // Only send the token to relative URLs i.e. locally.
		      xhr.setRequestHeader("X-XSRF-TOKEN",
		          Cookies.get('XSRF-TOKEN'));
		    }
		  }
		}
    });
	// Try to get logged in user details
  	$.ajax({
  		type: "GET",
  		contentType: "application/json; charset=utf-8",
  		url: "/api/users/currentUser", 
  		success: function (data) {
  	        $('#user').html(data.displayName);
  	        $('.badger-unauthenticated').hide();
  	        $('.badger-authenticated').show();
  	        if (data.admin==true) {
  	            $('#actionMenu li a').removeClass("badger-hidden-item");
  	            initAnnouncementsAdmin();
  	        }
  		},
  		complete: function (res) {
  	        // Get defaults
    	    $.ajax({
                type: "GET",
                contentType: "application/json; charset=utf-8",
                url: "api/defaults/",
                success: function (data) {
            	    map = new google.maps.Map(document.getElementById('map'), {
                    zoom: data.zoom,
                    center: { lat: data.lat, lng: data.lng },
                    gestureHandling: 'greedy'
                    });
                    showAnnouncements(data.announcements);
                },
                error: function (result) {
            	    errorPage("Failed to query default data", result);
                },
                complete: function (res) {
                    initAdvancedPage();
            	    initMarkers();
            	    initUpload();
                }
    	    });
  		}
    });   
  }
  
  function showAnnouncements(announcements) {
  	  var str = "";
      for (var i=0; i<announcements.length; i++) {
          str += announcements[i].message;
      }
      if (announcements.length>0) {
          $('#announcement').html(str);
          $('#announcementAlert').addClass("show");
          $('#announcementAlert').on('closed.bs.alert', function () {
              $.ajax({
                  type: "DELETE",
                  contentType: "application/json; charset=utf-8",
                  url: "api/users/announcements/"
	          });
          });
      }
  }
  
  function logout() {
	  $.post("/logout", function() {
	      $('#user').html('');
	      $('.badger-unauthenticated').show();
	      $('.badger-authenticated').hide();
	      location.reload();
	  });
      return true;
  }
  
  function initAreas() {
	  // Get all the possible gym areas
	  $.ajax({
          type: "GET",
          contentType: "application/json; charset=utf-8",
          url: "api/areas/",
          success: function (data) {
        	  var anyLoaded = loadAreaSelection();
        	  $('#filterButton').append($('<option>', {
    			  value: PARKS_ONLY_OPTION,
    			  text: 'Parks Only',
    			  selected: parksOnly
    		  }));
        	  $('#filterButton').append($('<optgroup>', {
        		  id: 'area-group',
    			  label: 'Select All Areas',
    			  value: SELECT_ALL_OPTION
    		  }));
        	  
        	  for (var i = 0; i < data.length; i++) {
        		  var areaSelected = true;
        		  if (anyLoaded) {
        		      areaSelected = false;
        		      for (var j = 0; j < selectedAreaIds.length; j++) {
        			      if (selectedAreaIds[j]==data[i].id) {
        				      areaSelected = true;
        				      break;
        			      }
        		      }
        		  } else {
        		      selectedAreaIds.push(data[i].id);
        		  }
        		  $('#area-group').append($('<option>', {
        			  value: data[i].id,
        			  text: data[i].name,
        			  selected: areaSelected
        		  }));
        	  }
          },
          error: function (result) {
          	errorPage("Failed to query area data", result);
          },
          complete: function (res) {
            $('#filterButton').multiselect({
              buttonClass: 'btn btn-primary',
              buttonContainer: '<div id="badger-dropdown-list" class="btn-group" />',
              enableClickableOptGroups: true,
              nonSelectedText: 'No areas',
              allSelectedText: 'All areas',
              maxHeight: 200,
              optionClass: function(element) {
            	  var value = $(element).val();
            	  if (parseInt(value,10)===PARKS_ONLY_OPTION) {
            		  return 'badger-filter-option badger-dropdown-list-split';
            	  }
                  return 'badger-filter-option';
              },
              onChange: function(option, checked) {
            	  if (option.length === 1) {
            		  var id = parseInt($(option).val(),10);
            		  if (id===PARKS_ONLY_OPTION) {
            			  onChangeParkOption(checked);
            			  return;
            		  }
            	  }
            	  onChangeArea(option, checked);
              }
            });
      	    $('#filterButton').multiselect('updateButtonText');
      	    initSearch();  
          }
  	});
  }
  
  // Make sure gyms in all selected areas are visible
  function updateVisibleGyms() {   
	  for (var i = 0; i < gymData.length; i++) {
		  var found = false;
		  for (var j = 0; j < selectedAreaIds.length; j++) {
			  if (gymData[i].area.id === selectedAreaIds[j]) {
				  found = true;
				  if (parksOnly && gymData[i].park===false) {
					  gymData[i].marker.setVisible(false);
				  } else {
					  gymData[i].marker.setVisible(true);
				  }
			  }
		  }
		  // Gyms outside the selected areas are hidden
		  if (!found) {
			  gymData[i].marker.setVisible(false);
		  }
	  }
  }

  function onChangeParkOption(checked) {
	  if (checked) {
	      parksOnly=true;
	  } else {
		  parksOnly=false;
	  }
	  storeAreaSelection();
	  resetSearch();
      resetPercentage();
	  updateVisibleGyms();
  }

  function onChangeArea(option, checked) {
	  // Update the list of selected area ids
	  selectedAreaIds.length=0;
	  var areas = $('#filterButton option:selected');
      $(areas).each(function(index, area){
          selectedAreaIds.push(parseInt(area.value,10));
      });
      storeAreaSelection();
      resetSearch();
      resetPercentage();
      updateVisibleGyms();
  }

  function initMarkers() {
	// Get all the gym positions and create markers for them
  	$.ajax({
        type: "GET",
          contentType: "application/json; charset=utf-8",
          url: "api/gyms/",
          success: function (data) {
        	  gymData = data;
              for (var i = 0; i < gymData.length; i++) {
            	  // Add the marker
                  var marker = new google.maps.Marker({
                	title: gymData[i].name,
                    position: { lat: gymData[i].lat, lng: gymData[i].lng },
                    icon: customIcon(gymData[i].status),
                    map: map
                  });
            	  gymData[i].marker = marker;
                  // Add a callback to create an info window for each marker if clicked
                  (function (marker, i) {
                    google.maps.event.addListener(marker, 'click', function () {
                    	history.replaceState({}, "GymBadger", "/?gymid="+gymData[i].id);
                        createInfoWindow(gymData[i], marker);
                    });
                  })(marker, i);
              } 
          },
          complete: function (result) {
        	  initAreas();
          },
          error: function (result) {
          	errorPage("Failed to query gym data", result);
          }
  	});
  }

  function queryGymDataAndUpdateInfoWindow(thisGym) {
      $.ajax({
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: "api/comments/"+thisGym.id,
        success: function (data) {
            var htmlContent="";
            if (data.comments.length==0 && !data.loggedin) {
                htmlContent = "<div class=\"alert alert-info badger-small-alert\" role=\"alert\">No comments posted yet.</div>";
            }
            for (var i = 0; i < data.comments.length; i++) {
                var commentDate = new Date(data.comments[i].createdate);
                htmlContent+="<div class=\"badger-comment\"><span class=\"badger-comment-user\">"+data.comments[i].displayname+"</span>" +
                    "<span class=\"badger-comment-text\">" + data.comments[i].text + "</span></div><div class=\"badger-sub-comment\">" + 
                    "<span class=\"badger-delete-link\">" + 
                    "<a class=\"badger-delete\" id=\"delete-link-" + data.comments[i].id + "\" data-commentid=\"" + data.comments[i].id + "\" href=\"#\">Delete</a></span>" + 
                    "<time class=\"timeago badger-comment-time\" datetime=\"" + commentDate.toISOString() + "\"></time>" +
                    "</div>";
            }
            if (data.loggedin) {
                htmlContent+="<div><div class=\"badger-comment\"><textarea class=\"badger-comment-input\" name=\"message\" type=\"text\" id=\"input-message\" placeholder=\"Write a comment...\"></textarea></div>" +
                    "<div class=\"badger-comment-options\">" +
                        "<select id=\"badger-public-menu\" class=\"badger-public-menu\">" +
                            "<option class=\"dropdown-item badger-public-item\" selected>Public</option>" +
                            "<option class=\"dropdown-item badger-public-item\">Private</option>" +
                        "</select>" + 
                        "<button id=\"badger-post-button\" type=\"button\" class=\"btn btn-primary badger-comment-post\">Post</button>" +
                    "</div></div>";
            }
            $('#comments').html(htmlContent);
            $('time.badger-comment-time').timeago();
            $('.badger-delete').on('click', function() {
                var idtodelete = $(this).attr('data-commentid');
                $.ajax({
                    type: "DELETE",
                    contentType: "application/json; charset=utf-8",
                    url: "api/comments/"+idtodelete,
                    success: function (data) {
                        // Update the UI by removing old raid time and adding new one
                        $('#comments').html('Loading...');
                        queryGymDataAndUpdateInfoWindow(thisGym);
                    },
                    error: function (result) {
                        errorPage("Failed to delete comment", result);
                    }
	            });     
            });
            
            $('#badger-public-menu').select2({
                minimumResultsForSearch: Infinity,
                containerCssClass: 'badger-public-menu',
                dropdownCssClass: 'badger-public-item'
            });
            $('#badger-post-button').on('click', function () {
                var comment = $('#input-message').val();
                var selectedText = $('#badger-public-menu').val();
                var ispublic = false;
                if (selectedText == 'Public') {
                    ispublic = true;
                }
                if (comment.length == 0) {
                    return;
                }
                var postData = {
                    gymid: thisGym.id,
                    text: comment,
                    ispublic: ispublic
                };
                $.ajax({
                    type: "POST",
                    contentType: "application/json; charset=utf-8",
                    url: "api/comments/",
                    data: JSON.stringify(postData),
                    success: function (data) {
                        // Update the UI by removing old raid time and adding new one
                        $('#comments').html('Loading...');
                        queryGymDataAndUpdateInfoWindow(thisGym);
                    },
                    error: function (result) {
                        errorPage("Failed to post new comment", result);
                    }
	            }); 
            });
        },
        error: function (result) {
            $('#comments').html("<div class=\"alert alert-danger badger-small-alert\" role=\"alert\">Failed to query gym comments.</div>");
            }
          });
      }
 
      function createInfoWindow(data, marker) {
          var content = 
            "<div class=\"badger-info-title\">" + 
          "<div class=\"dropdown badger-info-title-container\">" +
              "<button class=\"btn btn-default badger-small-button\" type=\"button\" id=\"dropdownMenuButton\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">" +
                  "<span class=\"badger-badge-container\"><span id=\"infoBadge\" class=\"badger-badge " + getBadgeClass(data.status) + "\"></span></span>" +
              "</button>" +
              "<div class=\"dropdown-menu\" aria-labelledby=\"dropdownMenuButton\" id=\"badgeDropdown\">" +
                   getBadgeDropdownHtml(data) +
                   getTabHeadingHtml() +
              "</div>" +
              "<span id=\"gymTitle\" class=\"badger-info-title-placeholder\"><a href=\"#\" class=\"badger-info-title-text\">" + data.name + "</a></span>" +
          "</div>" +
        "</div>" +
        "<div class=\"tab-content\">" +
            getGeneralTabHtml(data) +
            getHistoryTabHtml(data) +
        "</div>";
           
      currentMarker = marker;
      currentProps = data;
      closeAnyInfoWindows();
                		
      var popup = document.createElement('div');
      popup.setAttribute('class', 'badger-info');
      popup.innerHTML = content;
                	
      currentInfoWindow = new SnazzyInfoWindow({
           map: map,
           marker: currentMarker,
           wrapperClass: 'badger-info-wrapper',
           closeButtonMarkup: "<button type=\"button\" class=\"btn btn-outline-secondary badger-info-close\">&#215;</button>",
           closeWhenOthersOpen: false,
           closeOnMapClick: true,
           content: popup,
           padding: '6px',
           maxWidth: 350,
           mapTop: 66,
           edgeOffset: {
               top: 65,
               right: 25,
               bottom: 25,
               left: 25
           },
           callbacks: {
               afterOpen: function() {
                   $('.badger-dropdownitem').click(function() {
                       selectBadge(currentMarker, currentProps, getStatusFromId(this.id));
                   });
             	   $('.badger-info-close').click(function() {
                       closeAnyInfoWindows();
                   });
                   registerRaidEvents();
                   queryGymDataAndUpdateInfoWindow(data);
               },
               afterClose: function() {
                   // Forget any advanced options since gym info window is closing
                   resetAdvancedOptions();
               }
          }
      });
      currentInfoWindow.open();
  }

  function getBadgeDropdownHtml(data) {
	  var html = "<a id=\"select-gold\" class=\""+ getDropdownBadgeClass(data.status, "GOLD") + "\" href=\"#\">Gold</a>" +
        "<a id=\"select-silver\" class=\""+ getDropdownBadgeClass(data.status, "SILVER") + "\" href=\"#\">Silver</a>" +
        "<a id=\"select-bronze\" class=\""+ getDropdownBadgeClass(data.status, "BRONZE") + "\" href=\"#\">Bronze</a>" +
        "<a id=\"select-basic\" class=\""+ getDropdownBadgeClass(data.status, "BASIC") + "\" href=\"#\">Basic</a>" +
        "<a id=\"select-none\" class=\""+ getDropdownBadgeClass(data.status, "NONE") + "\" href=\"#\">None</a>";
      return html;
  }

  function getTabHeadingHtml() {
      var html = "<span class=\"badger-tab-container\">" +
          "<ul class=\"nav nav-tabs badger-tab-list\" role=\"tablist\">" +
            "<li class=\"nav-item\">" +
              "<a class=\"nav-link active badger-tab-label\" data-toggle=\"tab\" href=\"#general\" role=\"tab\" aria-selected=\"true\">General</a>" +
              "</li>" +
              "<li class=\"nav-item\">" +
                  "<a id=\"historyTab\" class=\"nav-link badger-tab-label\" data-toggle=\"tab\" href=\"#history\" role=\"tab\" aria-selected=\"false\">History</a>" +
              "</li>" +
            "</ul>" +
         "</span>";
       return html;
  }
  
  function getGeneralTabHtml(data) {
      var html =
        "<div class=\"tab-pane active\" id=\"general\" role=\"tabpanel\">" +
          "<div id=\"badger-area-container\">" + 
              "<span class=\"badge badge-primary badger-small-pill\">" + data.area.name + "</span>" +
              getParkStatus(data.park) + 
           "</div>" + 
           getLastRaid(data) +
           "<div class=\"badger-comments-group\">" + 
             "<div class=\"badger-comments-label\">Comments</div>" + 
             "<div id=\"comments\" class=\"badger-comments-container scrollable\">Loading...</div>" +
           "</div>" +
         "</div>";
      return html;
  }

  function getHistoryTabHtml(data) {
      var html =
        "<div class=\"tab-pane\" id=\"history\" role=\"tabpanel\">" +
          "<div id=\"historyBody\" class=\"badger-history-container scrollable\">" + 
             "Loading..." +    
           "</div>" +
         "</div>";
      return html;
  }
  
  function resetBadgeDropdown(data) {
      $('#badgeDropdown a').removeClass("badger-dropdownitem-checked");
      $('#badgeDropdown a').each(function( index ) {
          if (data.status==getStatusFromId($(this).attr("id"))) {
              $(this).addClass("badger-dropdownitem-checked");
          }
      });
  }

  function initAdvancedPage() {
      $('#badger-opt-save').click(function () {
      	var lastRaid = "";
      	var date = $('#badger-opt-date').data("datetimepicker").getDate();
      	var time = $('#badger-opt-time').data("datetimepicker").getDate();
      	if (time!=null) {
      		lastRaid = time;
      	} else {
      		lastRaid = date;
      	}
      	var pokemonId = parseInt($('#poke-search').val(), 10);
      	var isCaught = $('#caught-switch').bootstrapSwitch('state');
      	saveAdvanced(lastRaid, pokemonId, isCaught);
    });
  }

  function registerRaidEvents() {
    // remove old popovers from the DOM
    $('.popover').remove();
    
    // Enable date/time plugin for the raid dates
    $('time.badger-raid-time').timeago();
  	
  	// Setup the three raid button menu options
  	$('#now').click(function() {
  		selectRaid(currentProps, new Date());
    });
         
    $('#raidPopover1').popover({
  	    trigger: "manual",
        placement: 'auto',
        viewport: '#badger-container', 
        html: true,
        title: 'Choose a date <a href="#" class="close" data-dismiss="alert">&times;</a>',
        content: "<div class=\"badger-date-popover\"></div>"
    });
    $('#raidPopover1').on('shown.bs.popover', function(){
        $('.badger-date-popover').datetimepicker({
  		    format: 'dd-mm-yyyy',
  		    autoclose: true,
  		    todayHighlight: true,
  		    minView: 2
  	    }).on('changeDate', function(e) {
  		    selectRaid(currentProps, e.date);
  	    });
    });
    $('#choose').click(function() {
  		$('#raidPopover1').popover("show");
    });
    
    $('#options').click(function() {
        initAdvancedOptions();
  		$('#advancedOptions').modal('show');
    });

    $('#gymTitle').click(function() {
    	initGymInfoPage(currentProps);
  		$('#gymInfoPage').modal('show');
    });
    
    $(document).on("click", ".popover .close" , function(){
    	$(this).parents(".popover").popover('hide');
    });
    
    $('#historyTab').on('shown.bs.tab', function(e) {
        $.ajax({
          type: "GET",
          contentType: "application/json; charset=utf-8",
          url: "api/gyms/"+currentProps.id+"/history/",
          success: function (data) {
            var html = "<table id=\"historyTable\" class=\"table table-striped table-bordered badger-history-table scrollable\" style=\"width: 100%\">";
            html += "<thead style=\"display:none;\"><tr><th style=\"display:none;\">Id</th>" +
              "<th>Date</th>" +
              "<th>Event</th>" +
              "<th>Comment</th>" +
              "</tr></thead><tbody id=\"historyTableBody\" class=\"badger-history-table-body\">";
            for (var i=0; i<data.length; i++) {
                var raidDate = new Date(data[i].dateTime);
      	        html += "<tr><td style=\"display:none;\">" + data[i].id + "</td><td class=\"badger-history-text\">" + raidDate.toLocaleString('en-GB') + "</td>";
      	        if (data[i].type=='BADGE') {
      	            html += "<td><div id=\"infoBadge\" class=\"badger-badge " + getBadgeClass(data[i].status) + "\"></div></td>";
      	            if (data[i].status!="NONE") {
      	                html += "<td class=\"badger-history-text\">You earned a " + getBadgeText(data[i].status) + " badge</td>";
      	            } else {
      	            	html += "<td class=\"badger-history-text\">You have no badge for this gym</td>";
      	            }
      	        } else {
      	            var pokemonName = "Pokemon";
      	            if (data[i].pokemon!=null) {
      	                pokemonName = data[i].pokemon.text;
      	            }
      	            var pillClass = "badge-info";
      	            var comment = "";
      	            if (data[i].caught) {
      	                pillClass = "badge-success";
      	                comment = pokemonName + " was successfully caught";
      	            } else {
      	                pillClass = "badge-danger";
      	                comment = "Oh no! The wild " + pokemonName + " fled";
      	            }
      	            html += "<td><div class=\"badger-pokemon-container\"><span class=\"badge " + 
      	                pillClass + " badger-pokemon-badge\">" + pokemonName + "</span></div></td>";
      	            html += "<td class=\"badger-history-text\">" + comment + "</td>";
      	        }
      	        html+="</tr>";
      	    }
      	    html += "</tbody></table>";
      	    $('#historyBody').html(html);
          },
          error: function (result) {
      	    errorPage("Failed to query gym history", result);
          }
      });
    });
  }

  function initGymInfoPage(data) {
	  $("#gymInfoBody").html('');
      $('#gymInfoTitle').text(data.name);
      $("#gymInfoBody").append("<img class=\"badger-gym-info-image\" src=\"" + data.imageUrl + "\"/>");
  }

  function getLastRaid(data) {
	  return getLastRaidDetails(data, "", true);
  }
  
  function getLastRaidForReport(data) {
	  return getLastRaidDetails(data, "-small", false);
  }
  
  function getLastRaidDetails(data, containerClass, includeButton) {
	  var htmlContent = "";
	  if (data.lastRaid!=null) {
		  var lastRaidDate = new Date(data.lastRaid);
    	  if (!includeButton) {
    		  htmlContent = "<td data-order=\"" + data.lastRaid + "\">";
    	  }
		  htmlContent += "<div id=\"badger-info-raid" + data.id + "\" class=\"alert alert-success badger-raid-container badger-last-raid" + containerClass +" collapsed\"" + 
		        "data-toggle=\"collapse\" href=\"#collapseDate" + data.id + "\" aria-expanded=\"false\" aria-controls=\"collapseDate" + data.id + "\">" +
		          "<div class=\"badger-info-raid\">Last raided <time class=\"timeago badger-raid-time\" datetime=\"" + lastRaidDate.toISOString() + "\"></time>" +
		          "<div id=\"collapseDate" + data.id + "\" class=\"collapse hide\" role=\"tabpanel\" aria-labelledby=\"badger-info-raid" + data.id + "\" data-parent=\"#badger-info\">" +
    		        "<div class=\"badger-time-badge-container\"><span class=\"badge badge-success badger-time-badge\">" +
    		          lastRaidDate.toLocaleString() +
    		          "</span></div>" + getRaidPokemonHtml(data) +
    		      "</div>" +
		          "</div>" +
	              getRaidButtonHtml(includeButton) + "</div>";
	      return htmlContent;
	  } else {
		  if (!includeButton) {
    		  htmlContent = "<td data-order=\"0\">";
    	  }
	      htmlContent += "<div id=\"badger-info-raid\" class=\"alert alert-warning badger-raid-container badger-no-raid" + containerClass +"\"><div>No raid recorded yet</div>" +
	          getRaidButtonHtml(includeButton) + "</div>";
	  }
	  if (!includeButton) {
		  htmlContent += "</td>";
	  }
      return htmlContent;
  }

  function getRaidButtonHtml(includeButton) {
	  if (!includeButton) {
		  return "";
	  }
	  return "<div class=\"dropdown badger-info-raid-dropdown\">" +
        "<button class=\"btn btn-info badger-info-raid-button\" type=\"button\" id=\"raidMenuButton\" data-toggle=\"dropdown\"" +
        " aria-haspopup=\"true\" aria-expanded=\"false\">" + 
            "<span class=\"badger-raid-button-container\">" +
                "<span>Raid</span>" +
            "</span>" +
        "</button>" +
            "<div class=\"dropdown-menu\" aria-labelledby=\"raidMenuButton\">" +
              "<a id=\"now\" class=\"dropdown-item badger-info-raid-item\" href=\"#\">Now</a>" +
          "<a id=\"choose\" class=\"dropdown-item badger-info-raid-item\" data-provide=\"datepicker\" href=\"#\">Choose a Date</a>" +
          "<a id=\"options\" class=\"dropdown-item badger-info-raid-item\" href=\"#\">Advanced</a>" +
        "</div>" +
        "<span id=\"raidPopover1\"></span><span id=\"raidPopover2\"></span>" +
      "</div>";
  }

  function selectRaid(props, lastRaid) {
	  props.lastRaid = lastRaid;
      $.ajax({
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        url: "api/gyms/"+props.id,
        data: JSON.stringify(props, ["id", "name", "lat", "lng", "park", "status", "lastRaid"]),
        success: function (data) {
          currentProps = data;
          // Update the UI by removing old raid time and adding new one
          $('.badger-raid-container').remove();
          $('#badger-area-container').after(getLastRaid(data));
          registerRaidEvents();
        },
        error: function (result) {
          errorPage("Failed to update last raid", result);
        }
	  }); 	  
  }

  function resetPercentage() {
	  // Calculate the percentage
	  var data = getPercentGold();
	  // Clear previous percentage
	  $('#badger-gold-percent').html('');
	  // Draw the new percentage
	  var el = document.getElementById('badger-gold-percent');
	  drawPercentage(el, 42, 5, data.amount, data.total, '#efefef', '#f0f000', true, false);
  }

  function getSearchData() {
	  var searchData = $.map(gymData, function (obj) {
		  // Only include the gyms in the list of filtered areas
		  for (var i = 0; i < selectedAreaIds.length; i++) {
		      if (selectedAreaIds[i]===obj.area.id) {
		    	  if (!parksOnly || obj.park===true) {
		              obj.text = obj.name;
		              return obj;
		    	  }
		      }
		  }
		  return null;
      });
	  return searchData;
  }

  function initSearch() {
	  $('#search').select2({
		  placeholder: "Search",
		  width: '100%',
		  data: getSearchData()
	  });
	  $('#search').on("select2:select", function(e) {
	      map.setCenter({ lat: e.params.data.lat, lng: e.params.data.lng});
	      google.maps.event.trigger(e.params.data.marker, 'click');
      });
	  $('#navUploadButton').on("click", showUpload);
	  $('#navReportButton').on("click", showReports);
	  $('#navLeaderButton').on("click", showLeaderboard);
	  initRaidBosses();
      resetPercentage();
	  updateVisibleGyms();
  }

  // Show any gym specified in the URL
  function showGymFromUrl() {
      ;
      var gymIdStr = new URLSearchParams(window.location.search).get("gymid");
  	  if (gymIdStr !== null) {
  	      var urlGymId = parseInt(gymIdStr, 10);
  	      for (var i = 0; i < gymData.length; i++) {
  	    	  if (gymData[i].id == urlGymId) {
  	    		  var found = false;
  	    		  for (var j = 0; j < selectedAreaIds.length; j++) {
  	    			  if (selectedAreaIds[j]==gymData[i].area.id) {
  	    				  found=true;
  	    				  break;
  	    			  }
  	    		  }
  	    		  if (!found) {
  	    			  selectedAreaIds.push(gymData[i].area.id);
  	    		  }
  	    		  map.setCenter({ lat: gymData[i].lat, lng: gymData[i].lng});
  	    		  gymData[i].marker.setVisible(true);
                  google.maps.event.trigger(gymData[i].marker, 'click');
                  break;
  	    	  }
  	      }
  	      $('#filterButton').val(selectedAreaIds);
  	      $('#filterButton').multiselect("refresh");
  	  }
  }

  function resetSearch() {
	  closeAnyInfoWindows();
	  $('#search option').remove(); 
	  $('#search').html("<option></option>");
	  $('#search').select2({
		  placeholder: "Search",
		  width: '100%',
		  data: getSearchData()
	  });
	  $('#search').change();
  }

  function selectBadge(marker, props, status) {
	  var oldStatus = props.status;
	  props.status=status;
	  $.ajax({
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        url: "api/gyms/"+props.id,
        data: JSON.stringify(props, ["id", "name", "lat", "lng", "park", "status", "lastRaid", "pokemonId", "caught"]),
        success: function (data) {
          marker.setIcon(customIcon(props.status));
          $('#infoBadge').removeClass(getBadgeClass(oldStatus));
          $('#infoBadge').addClass(getBadgeClass(props.status));
          resetBadgeDropdown(props);
          resetPercentage();
        },
        error: function (result) {
          errorPage("Failed to update gym data", result);
        }
	  });
  }

  function closeAnyInfoWindows() {
      if (currentInfoWindow != null) {
	      currentInfoWindow.destroy();
	      currentInfoWindow = null;
	  }
  }
  
  function getRaidPokemonHtml(data) {
	  if (data.pokemonId == null) {
		  return "";
	  }
	  // Set the pill colour to be green for cuaght, red for fled
	  var pillClass = "badge-info";
	  if (data.caught != null) {
		  if (data.caught) {
			  pillClass = "badge-success";
		  } else {
			  pillClass = "badge-danger";
		  }
	  }
	  var pokemonName = null;
	  for (var i = 0; i < raidBossData.length; i++) {
	      if (raidBossData[i].id == data.pokemonId) {
	    	  pokemonName = raidBossData[i].text;
	    	  break;
	      }
	  }
      if (pokemonName == null) {
    	  return "";
      }
	  return "<div class=\"badger-pokemon-container\"><span class=\"badge " + pillClass + " badger-pokemon-badge\">" +
          pokemonName +
          "</span></div>";
  }

  function getBadgeColor (status) {
	  if (status==="BASIC") {
		  return '#b3ffb3';
	  }
	  if (status==="BRONZE") {
		  return '#e66b00';
	  }
	  if (status==="SILVER") {
		  return '#f5f5f0';
	  }
	  if (status==="GOLD") {
		  return '#ffd700';
	  }
	  return '#b3daff';
  }
  
  function getBadgeText (status) {
	  if (status==="BASIC") {
		  return 'Basic';
	  }
	  if (status==="BRONZE") {
		  return 'Bronze';
	  }
	  if (status==="SILVER") {
		  return 'Silver';
	  }
	  if (status==="GOLD") {
		  return 'Gold';
	  }
	  return 'None';
  }

  function customIcon (status) {
	  var ret = {
		path: 'M 0,0 C -2,-20 -10,-22 -10,-30 A 10,10 0 1,1 10,-30 C 10,-22 2,-20 0,0 z M -2,-30 a 2,2 0 1,1 4,0 2,2 0 1,1 -4,0',
		fillColor: getBadgeColor(status),
  	    fillOpacity: 1,
  	    strokeColor: '#000',
  	    strokeWeight: 2,
  	    scale: 1
	  };
	  return ret;
  }
  
  function getBadgeEffort (status) {
	  if (status==="BASIC") {
		  return 1;
	  }
	  else if (status==="BRONZE") {
		  return 3;
	  }
	  else if (status==="SILVER") {
		  return 7;
	  }
	  else if (status==="GOLD") {
		  return 21;
	  }
	  return 0;
  }
  
  function getBadgeOrder (status) {
	  if (status==="BASIC") {
		  return 1;
	  }
	  else if (status==="BRONZE") {
		  return 2;
	  }
	  else if (status==="SILVER") {
		  return 3;
	  }
	  else if (status==="GOLD") {
		  return 4;
	  }
	  return 0;
  }
  
  function getBadgeClass (status) {
	  if (status==="BASIC") {
		  return 'badger-badge-basic';
	  }
	  else if (status==="BRONZE") {
		  return 'badger-badge-bronze';
	  }
	  else if (status==="SILVER") {
		  return 'badger-badge-silver';
	  }
	  else if (status==="GOLD") {
		  return 'badger-badge-gold';
	  }
	  return 'badger-badge-none';
  }
  
  function getStatusFromId (id) {
	  if (id==="select-basic")
		  return "BASIC";
	  if (id==="select-bronze")
		  return "BRONZE";
	  if (id==="select-silver")
		  return "SILVER";
	  if (id==="select-gold")
		  return "GOLD";
	  return "NONE";
  }
  
  function getDropdownBadgeClass (currentStatus, targetStatus) {
      var classes = "dropdown-item badger-dropdownitem";
	  if (currentStatus===targetStatus) {
		  classes+=" badger-dropdownitem-checked";
	  }
	  return classes;
  }
  
  function getParkStatus (park) {
	  if (park) {
		  return "<span class=\"badge badge-success badger-small-pill-lm\">park</span>";
	  }
	  return "";
  }

  function getPercentGold() {
	  return getPercent(false, 'GOLD');    	  
  }
  
  function getPercentVisited() {
      return getPercent(true, 'NONE');
  }

  function getPercent(includeAll, status) {
	  var results = { total: 0, amount: 0 };
	  for (var i = 0; i < gymData.length; i++) {
		  for (var j = 0; j < selectedAreaIds.length; j++) {
			  if (gymData[i].area.id === selectedAreaIds[j]) {
				  if (!parksOnly || gymData[i].park===true) {
				      results.total++;
				      if ((includeAll && gymData[i].status!==status) || (!includeAll && gymData[i].status===status)) {
				          results.amount++;
			          }
				  }
			  }
		  }
	  }
	  return results;
  }

  function getPercentTotal() {
	  var results = { total: 0, amount: 0 };
	  for (var i = 0; i < gymData.length; i++) {
		  for (var j = 0; j < selectedAreaIds.length; j++) {
			  if (gymData[i].area.id === selectedAreaIds[j]) {
				  if (!parksOnly || gymData[i].park===true) {
				      results.total = results.total + getBadgeEffort('GOLD');
				      results.amount = results.amount + getBadgeEffort(gymData[i].status);
				  }
			  }
		  }
	  }
	  return results;
  }
  
  function errorPage(title, results) {
	  $('#errorPageTitle').text(title);
	  if (results.responseJSON !== undefined) {
	      $('#errorPageBody').text(results.responseJSON.message);
	  }
	  $('#errorPage').modal('show');
  }
  
  function showReports() {
	  closeAnyInfoWindows();
	  var gymsInTable = [];
	  var areaSet = new Set();
	  for (var i = 0; i < gymData.length; i++) {
	      for (var j = 0; j < selectedAreaIds.length; j++) {
    	      if (gymData[i].area.id === selectedAreaIds[j]) {
    		      if (!parksOnly || gymData[i].park===true) {
    		          $('#reportsTableBody').append("<tr>" +
    		        	  "<td style=\"display:none;\">" + gymData[i].id + "</td>" +
    	                  "<td>" + gymData[i].name + "</td>" +
    	                  "<td data-order=\"" + getBadgeOrder(gymData[i].status) + "\">" + 
    	                      "<div id=\"infoBadge\" class=\"badger-badge " + getBadgeClass(gymData[i].status) + "\"></div>" +
    	                  "</td>" +
    	                  "<td><div id=\"badger-area-container\">" + 
        		              "<div class=\"badge badge-primary badger-small-pill\">" + gymData[i].area.name + "</div></div>" +
        		          "</td><td><div id=\"badger-area-container\">" +
        		              getParkStatus(gymData[i].park) + "</div></td>" + 
        		              getLastRaidForReport(gymData[i]) +
    	              "</tr>");
    		          gymsInTable.push(gymData[i].id);
    		          areaSet.add(selectedAreaIds[j]);
    	          }
    	      }
    	  }
      }

	  $('time.badger-raid-time').timeago();
	  
	  reportTable = $('#reportsTable').DataTable({
	    "autoWidth": true,
		"scrollY": 300,
	    "scrollX": true,
	    "columns": [ {"searchable": false}, {"searchable": true}, {"searchable": false}, {"searchable": false}, {"searchable": false}, {"searchable": false} ]
	  });
	  
	  reportTable.on( 'search.dt', function () {
		  var query = reportTable.search();
		  if (query == "") {
			  // no query so use gyms from all areas
    	      var areaIds="";
    	      for (let item of areaSet.values()) {
    		      if (areaIds != "") {
    			      areaIds+=",";
    		      }
    		      areaIds+=item;
    	      }
    	      $('#exportButton').prop('href',"/api/gyms/export?areas="+areaIds);
		  } else {
			  var gymIds = "";
			  reportTable.rows({search: 'applied'}).data().each(function(value, index) {
			  	  console.log(value[0], index);
			  	  if (gymIds != "") {
  			          gymIds+=",";
  		          }
  		          gymIds+=value[0];
			  });
			  $('#exportButton').prop('href',"/api/gyms/export?gyms="+gymIds);
		  }
    	  
	  } );
	  
	  // search will not be filled in yet so all gyms for all areas are shown
	  var areaIds="";
	  for (let item of areaSet.values()) {
		  if (areaIds != "") {
			  areaIds+=",";
		  }
		  areaIds+=item;
	  }
	  $('#exportButton').prop('href',"/api/gyms/export?areas="+areaIds);
	  
	  // Show the percentage reports
	  var data = getPercentVisited();
	  $('#badger-rep-visited-percent').html('');
	  var el = document.getElementById('badger-rep-visited-percent');
	  drawPercentage(el, 62, 8, data.amount, data.total, '#a9a9a9', '#9acd32', true, true);

	  var data = getPercentGold();
	  $('#badger-rep-gold-percent').html('');
	  // Draw the new percentage
	  var el = document.getElementById('badger-rep-gold-percent');
	  drawPercentage(el, 62, 8, data.amount, data.total, '#a9a9a9', '#f0f000', true, true);

	  var data = getPercentTotal();
	  $('#badger-rep-total-percent').html('');
	  var el = document.getElementById('badger-rep-total-percent');
	  drawPercentage(el, 62, 8, data.amount, data.total, '#a9a9a9', '#87cefa', false, true);

      $('#reportsPage').on('hidden.bs.modal', function() {
	      $('#reportsTable').DataTable().destroy();
	      $('#reportsTableBody').html("");
      });

	  // Show the window
	  $('#reportsPage').on('shown.bs.modal', function () {
		  reportTable.columns.adjust().draw();
	  });
	  $('#reportsPage').modal('show');
  }

  function initRaidBosses() {
	  $.ajax({
          type: "GET",
          contentType: "application/json; charset=utf-8",
          url: "api/bosses/",
          success: function (data) {
      	    raidBossData = data;
      	    showGymFromUrl();
          },
          error: function (result) {
      	    errorPage("Failed to query raid boss data", result);
          }
      });
  }
  
  function initAdvancedOptions() {
	// Fill in fields with currentProps
  	$('#badger-opt-date').datetimepicker({
      	format: 'dd-mm-yyyy',
    		autoclose: true,
    		todayHighlight: true,
    		minView: 2
      }).on('changeDate', function(ev){
      	$('#badger-opt-time').datetimepicker('update', ev.date);
      });
      $('#badger-opt-time').datetimepicker({
      	format: 'dd-mm-yyyy hh:ii',
    		autoclose: true,
    		startView: 1
      });
      if (currentProps.lastRaid!=null) {
		    var lastRaidDate = new Date(currentProps.lastRaid);
		    $('#badger-opt-date').datetimepicker('update', lastRaidDate);
		    $('#badger-opt-time').datetimepicker('update', lastRaidDate);
      }

      $('#poke-search').select2({
		  placeholder: "Pokemon",
		  width: '100%',
		  data: raidBossData
	    });
      var caught = false;
      if (currentProps.caught != null) {
    	  caught = currentProps.caught;
      }
      $('#caught-switch').bootstrapSwitch({
      	state: caught,
      	onColor: 'success',
      	offColor: 'danger',
      	labelText: 'Caught',
      	onText: 'Yes',
      	offText: 'No'
      });
      if (currentProps.pokemonId!=null) {
          $('#poke-search').val(currentProps.pokemonId.toString());
          $('#poke-search').trigger('change');
      }
  }

  function saveAdvanced(lastRaid, pokemonId, caught) {
	  var props = currentProps;
	  props.lastRaid=lastRaid;
	  props.pokemonId=pokemonId;
	  props.caught=caught;
      $.ajax({
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        url: "api/gyms/"+props.id,
        data: JSON.stringify(props, ["id", "name", "lat", "lng", "park", "status", "lastRaid", "pokemonId", "caught"]),
        success: function (data) {
          currentProps = data;
          // Update the UI by removing old raid time and adding new one
          $('.badger-raid-container').remove();
          $('#badger-area-container').after(getLastRaid(data));
          registerRaidEvents();
        },
        error: function (result) {
          errorPage("Failed to update advanced options", result);
        },
        complete: function (res) {
          $('#advancedOptions').modal('hide');
            }
    	  }); 	  
      }
 
      function resetAdvancedOptions() {
          $('#poke-search').val("");
      $('#poke-search').trigger('change');
      $('#badger-opt-date').datetimepicker('remove');
      $('#badger-opt-time').datetimepicker('remove');
      $('.badger-opt-date-text').val("");
      $('#caught-switch').bootstrapSwitch('destroy');
  }

  // Load previously selected areas from local storage
  function loadAreaSelection() {
	  var anyLoaded = false;
	  selectedAreaIds.length=0;
	  if (typeof(Storage) !== "undefined") {
		  var selectedAreaJson = localStorage.getItem("selectedAreaIds");
		  if (selectedAreaJson !== null) {
	          selectedAreaIds = JSON.parse(localStorage.getItem("selectedAreaIds"));
	          anyLoaded = true;
		  }
		  var parksOnlyStorage = localStorage.getItem("parksOnly");
		  if (parksOnlyStorage !== null) {
			  parksOnly = (parksOnlyStorage == 'true');
			  anyLoaded = true;
		  }
	  }
      return anyLoaded;
  }
  
  // Store currently selected areas into local storage
  function storeAreaSelection() {
	  if (typeof(Storage) !== "undefined") {
	      localStorage.setItem("selectedAreaIds", JSON.stringify(selectedAreaIds));
	      localStorage.setItem("parksOnly", parksOnly);
	  }
  }
  
  function showLeaderboard() {
	  closeAnyInfoWindows();
	  $('#leadersTableBody').html("Loading...");
	  queryLeaderboardData();
	  queryTotalLeaderboardData();
   	  // Setup callbacks
   	  $('#leadersPage').on('hidden.bs.modal', function() {
       	  if (leadersTable != null) {
       		  leadersTable.destroy();
       		  leadersTable = null;
       	  }
          if (totalTable != null) {
        	  totalTable.destroy();
        	  totalTable = null;
     	  }
       	  $('#share-switch').bootstrapSwitch('destroy');
   	      $('#leadersTableBody').html("");
   	      $('#totalTableBody').html("");
   	  });
      $('#share-switch').bootstrapSwitch({
      	state: false,
      	onColor: 'success',
      	offColor: 'danger',
      	labelText: 'Participate',
      	onText: 'Yes',
      	offText: 'No'
      });
      $('#share-switch').on('switchChange.bootstrapSwitch', function(e) {
	    $.ajax({
            type: "PUT",
            contentType: "application/json; charset=utf-8",
            url: "api/users/leaderboard/",
            data: JSON.stringify(e.target.checked),
            success: function (data) {
              // Update the UI by removing table and requerying everything
               if (leadersTable != null) {
       		       leadersTable.destroy();
       		       leadersTable = null;
       	       }
               if (totalTable != null) {
     		       totalTable.destroy();
     		       totalTable = null;
     	       }
               queryLeaderboardData();
               queryTotalLeaderboardData();
            },
            error: function (result) {
              alert("Failed to update leaderboard status", result);
            }
    	  });
      });
      // Show the window
      $('#leadersPage').modal('show');
  }
  
  function queryLeaderboardData() {
	  $.ajax({
          type: "GET",
          contentType: "application/json; charset=utf-8",
          url: "api/users/leaderboard",
          success: function (data) {
        	  $('#leadersTableBody').html("");
        	  $('#share-switch').bootstrapSwitch('state', data.share, false);
        	  if (data.leaders == null || data.leaders.length==0) {
        		  $('#leadersTableBody').append("<tr><td><div class=\"alert alert-warning\" role=\"alert\">No gym badge leaders to display.</div></td></tr>");
        	  } else {
        	      for (var i = 0; i < data.leaders.length; i++) {
        		      var rankClass = getRankClass(data.leaders[i].rank);
        		      $('#leadersTableBody').append("<tr>" +
        	              "<td class=\"" + rankClass + "\">" + getRankHtml(data.leaders[i].rank) + "</td>" +
        	              "<td class=\"" + rankClass + "\">"  + data.leaders[i].name + "</td>" +
        	              "<td class=\"" + rankClass + " badger-lead-total\">"  + data.leaders[i].badges + "</td>");
        	      }
        	      // Initialize the table
            	  leadersTable = $('#leadersTable').DataTable({
            	    "autoWidth": true,
            		"scrollY": 300,
            	    "scrollX": true,
            	    "searching": false,
            	    "lengthChange": false
            	  });
              }
       	  },
          error: function (result) {
        	  $('#leadersTableBody').html("");
        	  var errorHtml = "<tr><td><div class=\"alert alert-danger\" role=\"alert\">Failed to query leaderboard.";
        	  if (result.responseJSON !== undefined) {
        		  errorHtml += "<br>" + result.responseJSON.message;
        	  }
        	  errorHtml += "</div></td></tr>";
        	  $('#leadersTableBody').append(errorHtml);
          },
          complete: function() {
        	  if (leadersTable != null) {
       		      leadersTable.columns.adjust().draw();
       		  }
          }
	  });
  }
  
  function queryTotalLeaderboardData() {
  if (leadersTable != null) {
	  leadersTable.destroy();
	  leadersTable = null;
  }
  if (totalTable != null) {
	  totalTable.destroy();
	  totalTable = null;
  }
  $.ajax({
      type: "GET",
      contentType: "application/json; charset=utf-8",
      url: "api/users/leaderboard/totals",
      success: function (data) {
    	  $('#totalTableBody').html("");
    	  if (data.leaders == null || data.leaders.length==0) {
    		  $('#totalTableBody').append("<tr><td><div class=\"alert alert-warning\" role=\"alert\">No gym badge leaders to display.</div></td></tr>");
    	  } else {
    	      for (var i = 0; i < data.leaders.length; i++) {
    		      var rankClass = getRankClass(data.leaders[i].rank);
    		      $('#totalTableBody').append("<tr>" +
    	              "<td class=\"" + rankClass + "\">" + getRankHtml(data.leaders[i].rank) + "</td>" +
    	              "<td class=\"" + rankClass + "\">"  + data.leaders[i].name + "</td>" +
    	              "<td class=\"" + rankClass + " badger-lead-total\">"  + data.leaders[i].badges + "</td>");
    	      }
    	      // Initialize the table
        	  totalTable = $('#totalTable').DataTable({
        	    "autoWidth": true,
        		"scrollY": 300,
        	    "scrollX": true,
        	    "searching": false,
        	    "lengthChange": false
        	  });
          }
   	  },
      error: function (result) {
    	  $('#totalTableBody').html("");
    	  var errorHtml = "<tr><td><div class=\"alert alert-danger\" role=\"alert\">Failed to query total leaderboard.";
    	  if (result.responseJSON !== undefined) {
    		  errorHtml += "<br>" + result.responseJSON.message;
    	  }
    	  errorHtml += "</div></td></tr>";
    	  $('#totalTableBody').append(errorHtml);
      },
      complete: function() {
    	  if (totalTable != null) {
   		      totalTable.columns.adjust().draw();
   		  }
      }
    });
  }

  function getRankHtml(rank) {
	  if (rank==1) {
		  return "<span class=\"badger-lead-medal1\"></span>";
	  }
	  if (rank==2) {
		  return "<span class=\"badger-lead-medal2\"></span>";
	  }
	  if (rank==3) {
		  return "<span class=\"badger-lead-medal3\"></span>";
	  }
	  return ""+rank;
  }
  
  function getRankClass(rank) {
	  if (rank==1) {
		  return "badger-rank-1";
	  }
	  if (rank==2) {
		  return "badger-rank-2";
	  }
	  if (rank==3) {
		  return "badger-rank-3";
	  }
	  return "badger-rank-other";
  }
  
  function initAnnouncementsAdmin() {
   	  // Setup callbacks
   	  $('#announcementAdminPage').on('hidden.bs.modal', function() {
   	      $('#announce-message').val(''); 
   	  });
      $('#postAnnouncement').on('click', function(e) {
        var messageVal = $('#announce-message').val();
	    $.ajax({
            type: "PUT",
            contentType: "application/json; charset=utf-8",
            url: "api/users/announcements/1/",
            data: messageVal,
            success: function (data) {
                $('#announcementAdminPage').modal('hide');  
            },
            error: function (result) {
              alert("Failed to post announcement", result);
            }
    	  });
      });
  }
  
  function showAnnouncementsAdmin() {
	  closeAnyInfoWindows();
      // Show the window
      $('#announcementAdminPage').modal('show');
  }
  
  function initUpload() {
      $('#fileUploadButton').on('click', function() {
        var formData = new FormData();
        var form = $('#fileUploadData').get(0);
        var files = form[0].files;
        $.each(files, function() {
          var file = $(this);
          formData.append("files[]", file[0], file[0].name);
        });
    
        $.ajax({
          type: "POST",
          url: "/api/upload/badges",
          data: formData,
          async: false,
          success: function (data) {
            $('#uploadPage').modal('hide');
            showUploadResults(data);
          },
          cache: false,
          contentType: false,
          processData: false
        });
      });
      initUploadResults();
  }

  function showUpload() {
      closeAnyInfoWindows();
      // Show the window
      $('#fileDetails').html("");
      $('#uploadPage').modal('show');
  }

  function fileSelected() { 
	  $('#fileDetails').html("");
	  var count = document.getElementById('fileToUpload').files.length;
      var detailsDiv = document.getElementById('fileDetails');
      for (var i = 0; i<count; i++)
      {
          var file = document.getElementById('fileToUpload').files[i];
          var image = document.createElement('img');
          image.src = window.URL.createObjectURL(file);
          image.className = "badger-upload-preview";
          detailsDiv.appendChild(image);
      }
  }
  
  function initUploadResults() {
  }
  
  function showUploadResults(data) {
      $('#uploadResultDetails').html("");
      if (data.gyms==null || data.gyms==undefined || data.gyms.length==0) {
          $('#uploadResultDetails').html("<div class=\"alert alert-warn badger-upload-text-area\" role=\"alert\">" + 
              "<span><p>No gyms or badges detected, try a different screenshot.</p></span></div>");
          return;
      }
      var html="";
      for (var i=0; i<data.gyms.length; i++) {
          html+= "<div id=\"gymResult-" + i + "\" class=\"badger-gym-result\">" + 
              "<div class=\"badger-gym-result-box\">" +
                "<div class=\"badger-gym-result-div\">" + 
                  "<span class=\"badger-result-row\">" + getBadgeHtml(data.gyms[i]) + "</span>" + 
                "</div>" +
                "<div id=\"gymClose-" + i + "\" class=\"badger-gym-result-close-btn\"><button type=\"button\" class=\"close\" aria-label=\"Close\">" +
                  "<span class=\"badger-gym-result-close\">×</span></button>" +
                "</div>" + 
              "</div>" + 
              "<div class=\"badger-gym-result-grid\">" +
                  "<span class=\"badger-gym-result-label\">" + data.gyms[i].name + "</span>" + 
              "</div>" +
            "</div>";
      }
      $('#uploadResultDetails').html(html);
      for (var i=0; i<data.gyms.length; i++) {
          $("#gymClose-"+i).click({index: i}, function(event) {
              $("#gymResult-"+event.data.index).remove();
          });
      }
      $('#uploadResultsPage').modal('show');
      
      alert("Feature still under development...coming soon");
  }
  
  function getBadgeHtml(data) {
      var html="<button class=\"btn btn-default badger-small-button\" type=\"button\" id=\"dropdownMenuButton" + data.id + "\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">" +
        "<span class=\"badger-badge-container\"><span id=\"infoBadge" + data.id + "\" class=\"badger-badge " + getBadgeClass(data.status) + "\"></span></span>" +
        "</button>" +
        "<div class=\"dropdown-menu\" aria-labelledby=\"dropdownMenuButton" + data.id + "\" id=\"badgeDropdown" + data.id + "\">" +
          getBadgeDropdownHtml(data) +
        "</div>";
      return html;
  }