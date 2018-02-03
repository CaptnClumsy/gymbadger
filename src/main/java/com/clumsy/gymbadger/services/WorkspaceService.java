package com.clumsy.gymbadger.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkspaceService {

	private Map<Long, String> workspaces;
	
	public WorkspaceService() {
		workspaces = new HashMap<Long, String>();
	}

	public String getWorkspace(final Long userId) throws WorkspaceException {
		try {
			File tempDir = Files.createTempDirectory("gymbadger-"+userId).toFile();
			workspaces.put(userId, tempDir.getAbsolutePath());
			return workspaces.get(userId);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new WorkspaceException("Failed to create new workspace on server");
		}
	}
	
	public void release(Long userId) {
		String dir = workspaces.get(userId);
		if (dir!=null) {
			FileSystemUtils.deleteRecursively(new File(dir));
			workspaces.remove(userId);
		}
	}
}
