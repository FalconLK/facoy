import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

public class MedicalTrackParser extends org.xml.sax.helpers.DefaultHandler {

    public static Connection connection = null;

    static StringBuffer type = new StringBuffer();

    static StringBuffer reportText = new StringBuffer();

    static StringBuffer subtype = new StringBuffer();

    static StringBuffer visitId = new StringBuffer();

    ;

    static StringBuffer checksum = new StringBuffer();

    ;

    static StringBuffer gender = new StringBuffer();

    ;

    static StringBuffer age = new StringBuffer();

    ;

    public static HashMap<String, Integer> chkvstMap = new HashMap<String, String>();

    static FileWriter outputFile;

    static boolean chksum_flag = false;

    static boolean subtype_flag = false;

    static boolean type_flag = false;

    static boolean vst_flag = false;

    static boolean gender_flag = false;

    static boolean age_flag = false;

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: MedicalTrackParser Medcollection_Path " + "mappingFile OutputFileName");
            System.exit(0);
        }
        outputFile = new FileWriter(args[2], false);
        System.setProperty("org.xml.sax.driver", "com.bluecast.xml.Piccolo");
        MedicalTrackParser handler = new MedicalTrackParser();
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(handler);
        MedicalTrackParser vds = new MedicalTrackParser();
        vds.traversePath(args[0], xmlReader, outputFile);
        vds.traversePath(args[0], args[1], args[2], xmlReader, outputFile);
        outputFile.close();
    }

    /**
	 * traversePath 
	 * Find all files in the input path
	 * Judge whether the file is .zip or .tar.gz
	 * call traverseZipfile() or traverseTarGzFile() to traverse xml files in archive files
	 * @param outputFile 
	 * @throws SAXException 
	 * @throws IOException 
	 */
    public void traversePath(String strPath, XMLReader xmlReader, FileWriter fw) throws SAXException, IOException {
        File path = new File(strPath);
        File[] files = path.listFiles();
        if (files == null) {
            System.out.println("The path " + strPath + " has no files");
            System.exit(1);
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                traversePath(files[i].getAbsolutePath(), xmlReader, fw);
            } else {
                String strFileName = files[i].getAbsolutePath();
                try {
                    if (strFileName.contains("zip")) {
                        traverseZipFile(strFileName, xmlReader, fw);
                    } else if (strFileName.contains("tar.gz")) {
                        traverseTarGzFile(strFileName, xmlReader, fw);
                    } else if (strFileName.endsWith("xml")) {
                        xmlReader.parse(strFileName);
                    }
                } catch (SAXParseException e) {
                    System.out.println("Wrong Xml format file" + strFileName);
                }
            }
        }
        System.out.println("Start Writing File");
        it = this.tagMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            outputFile.write((String) entry.getKey());
            outputFile.write('\t');
            count = (Integer) entry.getValue();
            outputFile.write(Integer.toString(count));
            outputFile.write('\t');
            rate = count / totalNum;
            outputFile.write(Double.toString(rate));
            outputFile.write('\n');
            outputFile.flush();
        }
        System.out.println("Writing file finished");
    }

    public void readMappingFile(String mappingFileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(mappingFileName));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] resultPair = line.split("  ");
            String checksum = resultPair[0];
            String visitid = resultPair[2];
            System.out.println("topic ID" + checksum);
            System.out.println("shot ID" + visitid);
            this.chkvstMap.put(checksum, visitid);
        }
        br.close();
    }

    public void readTopicFile(String topicFileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(topicFileName));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] topicPair = line.split(" ");
            String topicId = topicPair[0];
            HashSet<String> wordList = new HashSet<String>();
            for (int i = 1; i < topicPair.length; i++) {
                if (!wordList.contains(topicPair[i])) wordList.add(topicPair[i]);
            }
            this.queryWords.put(topicId, wordList);
        }
        br.close();
    }

    public void traversePath(String strPath, String topicFileName, String resultFileName, XMLReader xmlReader, FileWriter fw) throws SAXException, IOException {
        File path = new File(strPath);
        File[] files = path.listFiles();
        if (files == null) {
            System.out.println("The path " + strPath + " has no files");
            System.exit(1);
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                traversePath(files[i].getAbsolutePath(), xmlReader, fw);
            } else {
                String strFileName = files[i].getAbsolutePath();
                try {
                    if (strFileName.contains("zip")) {
                        traverseZipFile(strFileName, xmlReader, fw);
                    } else if (strFileName.contains("tar.gz")) {
                        traverseTarGzFile(strFileName, xmlReader, fw);
                    } else if (strFileName.endsWith("xml")) {
                        xmlReader.parse(strFileName);
                    }
                } catch (SAXParseException e) {
                    System.out.println("Wrong Xml format file" + strFileName);
                }
            }
        }
        this.readResultFile(resultFileName);
        this.readTopicFile(topicFileName);
        this.compareTopicXmlContent(fw);
    }

    @SuppressWarnings("unchecked")
    private void traverseZipFile(String zipFileName, XMLReader xmlReader, FileWriter fw) throws SAXException, IOException {
        FileInputStream fileIn = null;
        BufferedInputStream bufIn = null;
        ZipInputStream zipIn = null;
        try {
            fileIn = new FileInputStream(zipFileName);
            bufIn = new BufferedInputStream(fileIn);
            zipIn = new ZipInputStream(bufIn);
            ZipEntry entry = null;
            while ((entry = zipIn.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                if (entry.getName().endsWith(".DS_Store")) {
                    System.out.println(entry.getName());
                    int len = zipIn.read(new byte[zipIn.available()]);
                    continue;
                } else if (entry.getName().endsWith(".xml")) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte buf[] = new byte[500000];
                    int blen;
                    while ((blen = zipIn.read(buf)) != -1) {
                        bos.write(buf, 0, blen);
                    }
                    byte cbuf[] = bos.toByteArray();
                    ByteArrayInputStream bis = new ByteArrayInputStream(cbuf);
                    InputSource inputSource = new InputSource(bis);
                    xmlReader.parse(inputSource);
                    ucid_flag = 0;
                    msid_flag = 0;
                    ucid.delete(0, ucid.length());
                    msid.delete(0, msid.length());
                    index = 1;
                    paraLength = 0;
                    ucidTag_flag = 0;
                    msidTag_flag = 0;
                } else continue;
            }
        } finally {
            zipIn.close();
            bufIn.close();
            fileIn.close();
        }
    }

    private void traverseTarGzFile(String zipFileName, XMLReader xmlReader, FileWriter fw) throws IOException {
        index = 1;
        paraLength = 0;
        FileInputStream fileIn = null;
        BufferedInputStream bufIn = null;
        GZIPInputStream gzipIn = null;
        TarInputStream taris = null;
        try {
            fileIn = new FileInputStream(zipFileName);
            bufIn = new BufferedInputStream(fileIn);
            gzipIn = new GZIPInputStream(bufIn);
            taris = new TarInputStream(gzipIn);
            TarEntry entry = null;
            while ((entry = taris.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                if (entry.getName().endsWith(".DS_Store")) {
                    System.out.println(entry.getName());
                    continue;
                } else if (entry.getName().endsWith(".xml")) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte buf[] = new byte[50000];
                    int blen;
                    while ((blen = taris.read(buf)) != -1) {
                        bos.write(buf, 0, blen);
                    }
                    byte cbuf[] = bos.toByteArray();
                    ByteArrayInputStream bis = new ByteArrayInputStream(cbuf);
                    InputSource inputSource = new InputSource(bis);
                    xmlReader.parse(inputSource);
                    ucid_flag = 0;
                    msid_flag = 0;
                    ucid.delete(0, ucid.length());
                    msid.delete(0, msid.length());
                    index = 1;
                    paraLength = 0;
                    ucidTag_flag = 0;
                    msidTag_flag = 0;
                } else continue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            taris.close();
            gzipIn.close();
            bufIn.close();
            fileIn.close();
        }
    }

    public void compareTopicXmlContent(FileWriter fw) throws IOException {
        Iterator iter = this.resultPair.entrySet().iterator();
        while (iter.hasNext()) {
            int count = 0;
            Map.Entry entry = (Map.Entry) iter.next();
            String topicId = (String) entry.getKey();
            String shot_id = (String) entry.getValue();
            fw.append(topicId + ' ');
            HashSet<String> words = this.queryWords.get(topicId);
            String xmlcontent = this.xmlContent.get(shot_id);
            if (xmlcontent == null) {
                fw.append("null file" + '\n');
                continue;
            }
            xmlcontent = xmlcontent.toLowerCase();
            Iterator it = words.iterator();
            int termcount = 0;
            while (it.hasNext()) {
                String queryTerm = (String) it.next();
                queryTerm = queryTerm.toLowerCase();
                Pattern pattern = Pattern.compile(' ' + queryTerm + ' ');
                if (queryTerm.length() == 0) {
                    System.out.println("query term is null" + queryTerm);
                    continue;
                }
                System.out.println(xmlcontent);
                Matcher matcher = pattern.matcher(xmlcontent);
                while (matcher.find()) termcount++;
                fw.append(queryTerm + " ");
                fw.append(termcount + " ");
                count += termcount;
                termcount = 0;
            }
            fw.append(count + " ");
            fw.append('\n');
            fw.flush();
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            if ((attributes.getQName(i).compareTo("ucid") == 0) & (ucid_flag == 0)) {
                ucid.append(attributes.getValue(attributes.getQName(i)));
                ucid_flag = 1;
            } else if ((attributes.getQName(i).compareTo("ms-id") == 0) & (msid_flag == 0)) {
                msid.append(attributes.getValue(attributes.getQName(i)));
                msid_flag = 1;
            }
        }
        if ((localName.matches("ucid")) & (ucidTag_flag == 0)) ucidTag_flag = 1; else if ((localName.matches("ms-id")) & (msidTag_flag == 0)) msidTag_flag = 1;
        target = new StringBuffer();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ((localName == "ucid") & (ucidTag_flag == 1)) {
            ucid.append(target);
            System.out.println(ucid.toString());
        } else if ((localName == "ms-id") & (msidTag_flag == 1)) {
            msid.append(target);
            System.out.println(msid.toString());
        }
        if ((localName == "p") & (para_flag == 1)) {
            try {
                if ((ucid_flag == 1) | (ucidTag_flag == 1)) {
                    outputFile.write(ucid.toString());
                    System.out.println(ucid.toString());
                } else if ((msid_flag == 1) | (msidTag_flag == 1)) {
                    outputFile.write(msid.toString());
                    System.out.println(msid.toString());
                }
                outputFile.write('.');
                outputFile.write(Integer.toString(index));
                outputFile.write('.');
                paraLength = target.length();
                outputFile.write(Integer.toString(paraLength));
                outputFile.write((char) 30);
                outputFile.write(target.toString().replaceAll("\n+", ""));
                outputFile.write((char) 30);
                outputFile.write((char) 29);
                outputFile.write('\n');
                outputFile.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            index = index + paraLength;
            target = new StringBuffer();
        }
        content.append(target.toString() + ' ');
        if (localName == "shotID") {
            shotid = target.toString();
        }
        if (localName == "metadata") {
            this.xmlContent.put(shotid, content.toString());
            content = new StringBuffer();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        target.append(ch, start, length);
    }
}
