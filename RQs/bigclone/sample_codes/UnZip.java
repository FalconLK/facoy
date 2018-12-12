
public class UnZip {
	public static void unzip1(File zipfile, File outputdir) throws IOException {
		//Buffer for copying the files out of the zip input stream
		byte[] buffer = new byte[1024];
				
		//Create parent output directory if it doesn't exist
		if(!outputdir.exists()) {
			outputdir.mkdirs();
		}
				
		//Create the zip input stream
		//OR ArchiveInputStream zis = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, new FileInputStream(zipfile));
		ArchiveInputStream zis = new ZipArchiveInputStream(new FileInputStream(zipfile));
		
		//Iterate through the entries of the zip file, and extract them to the output directory
		ArchiveEntry ae = zis.getNextEntry(); // OR zis.getNextZipEntry()
		while(ae != null) {
			//Resolve new file
			File newFile = new File(outputdir + File.separator + ae.getName());
			
			//Create parent directories if not exists
			if(!newFile.getParentFile().exists())
				newFile.getParentFile().mkdirs();
			
			if(ae.isDirectory()) { //If directory, create if not exists
				if(!newFile.exists())
					newFile.mkdir();
			} else { //If file, write file
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			
			//Proceed to the next entry in the zip file
			ae = zis.getNextEntry();
		}
		
		//Cleanup
		zis.close();
	}
	
	public static void unzip2(File zipfile, File outputdir) throws IOException {
		//Buffer for extracting files
		byte[] buffer = new byte[1024];
				
		//Zip file
		ZipFile zip = new ZipFile(zipfile);
				
		//Get entries
		Enumeration<ZipArchiveEntry> files = zip.getEntries();
				
		//Iterate through the entries
		while(files.hasMoreElements()) {
			//Get entry
			ZipArchiveEntry ze = files.nextElement();
					
			//Resolve entry file
			File newFile = new File(outputdir + File.separator + ze.getName());
					
			//Make parent directories
			newFile.getParentFile().mkdirs();
					
			if(ze.isDirectory()) { //If directory, create it
				newFile.mkdir();
			} else { //If file, extract it
				InputStream is = zip.getInputStream(ze);
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while((len = is.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				is.close();
			}	
		}
				
		//Cleanup
		zip.close();
	}
}

