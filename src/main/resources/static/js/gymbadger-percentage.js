function drawPercentage(parentElement, size, lineWidth, amount, total, bgColour, fgColour, includeCounts, isLargeFont) {
    // Work out the percentage
	var percent = 0;
   	if (total != 0) {
   		percent = Math.round((amount / total) * 100);
    }
    // Create the canvas
   	var canvas = document.createElement('canvas');
   	if (typeof(G_vmlCanvasManager) !== 'undefined') {
		G_vmlCanvasManager.initElement(canvas);
	}
   	var ctx = canvas.getContext('2d');
	canvas.width = canvas.height = size;
   	// Add the percentage text
    var span = document.createElement('span');
    span.className = "badger-percentage-text";
    span.style.width = ""+size+"px";
    span.style.height = ""+size+"px";
	span.style.lineHeight = ""+size+"px";
	span.style.fontSize = getFontSize(isLargeFont, percent);
	span.textContent = '' + percent + '%';
	if (includeCounts) {
		// Add the details of the amount verses total
		var detailsSpan = document.createElement('span');
		detailsSpan.style.width = ""+size+"px";
		detailsSpan.style.top = ""+(size-4)+"px";
		detailsSpan.className = "badger-percentage-details";
		detailsSpan.innerHTML = "" + amount + "/" + total;
		parentElement.appendChild(detailsSpan);
	}
    // Append the other elements to the specified parent element
	parentElement.appendChild(span);
	parentElement.appendChild(canvas);

	// Draw the circle on the canvas
	var rotate = 0;
	ctx.translate(size / 2, size / 2); // change center
	ctx.rotate((-1 / 2 + rotate / 180) * Math.PI); // rotate -90 deg
    var radius = (size - lineWidth) / 2;
    var drawCircle = function(color, lineWidth, percent) {
    	percent = Math.min(Math.max(0, percent || 1), 1);
		ctx.beginPath();
		ctx.arc(0, 0, radius, 0, Math.PI * 2 * percent, false);
		ctx.strokeStyle = color;
		ctx.lineCap = 'round'; // butt, round or square
		ctx.lineWidth = lineWidth
		ctx.stroke();
    };

	drawCircle(bgColour, lineWidth, 100 / 100);
	if (percent != 0) {
	    drawCircle(fgColour, lineWidth, percent / 100);
	}
}

function getFontSize(isLargeFont, value) {
	if (!isLargeFont) {
		if (value < 10) {
			return "13px";
		} else if (value > 99) {
			return "11px";
		} else {
			return "12px";
		}
	} else {
		if (value < 10) {
			return "21px";
		} else if (value > 99) {
			return "17px";
		} else {
			return "20px";
		}
	}
	
}