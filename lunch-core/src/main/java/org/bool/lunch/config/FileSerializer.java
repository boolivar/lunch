package org.bool.lunch.config;

import com.esotericsoftware.yamlbeans.scalar.ScalarSerializer;

import java.io.File;

public class FileSerializer implements ScalarSerializer<File> {

	@Override
	public String write(File file) {
		return file.toString();
	}

	@Override
	public File read(String path) {
		return new File(path);
	}
}
