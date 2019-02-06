## kernel-pdfgenerator-itext

[Background & Design](../../docs/design/kernel/kernel-pdfgenerator.md)
 

[API Documentation]
 
 ```
 mvn javadoc:javadoc

 ```

** Input template can be send  in the following format:**

1. InputStream
2. String
3. File


The response will be generated PDF file as OutputStream or generated PDF file as given output file path and and file name.
 
If there is any error which occurs while generate pdf, it will be thrown as Exception.

Exceptions to be handled while using this functionality:

1. PDFGeneratorException
2. IOException 

**Maven Dependency**
 
 ```
 <dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-pdfgenerator-itext</artifactId>
			<version>${project.version}</version>
 </dependency>
 ```
 
**Usage Sample:**
 
 Usage1:
 
Input processed Template as inputStream and generate OutputStream containing the resulting PDF
 
 ```
@Autowired
 PDFGenerator pdfGenerator;
 
     ClassLoader classLoader = getClass().getClassLoader();
	  String inputFile = classLoader.getResource("csshtml.html").getFile();
	  InputStream is = new FileInputStream(inputFile);
     OutputStream os = pdfGenerator.generate(inputStream);
  
 ```

Usage2:
 
Input processed Template as String and generate OutputStream containing the resulting PDF
 
 ```
@Autowired
 PDFGenerator pdfGenerator;
 
       ClassLoader classLoader = getClass().getClassLoader();
		String inputFileName = classLoader.getResource("test.html").getFile();
		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		String line;
		StringBuilder template = new StringBuilder();
		while ((line = br.readLine()) != null) {
			template.append(line.trim());
		}
       OutputStream os = pdfGenerator.generate(template); 
 ```
 
Usage3:
 
Generate PDF take processed template as file, output file path and output file name and generate the resulting PDF in given output path with the given output file name
 
 
 ```
 @Autowired
 PDFGenerator pdfGenerator;
 
        String outputPath = System.getProperty("user.dir");
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("textcontant.txt").getFile();
		String generatedPdfFileName = "textcontant";
        pdfGenerator.generate(templatePath,outpuFilePath,outputFileName);
       
 ```
 
Usage4:
 
Input processed Template as inputStream, resource file path  and generate OutputStream containing the resulting PDF.
 
 ```
@Autowired
 PDFGenerator pdfGenerator;
 
      ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("responsive.html").getFile();
		File file = new File(inputFile);
		if (file.getParentFile().isDirectory()) {
			file = file.getParentFile();
		}
		String resourceLoc = file.getAbsolutePath();
		InputStream is = new FileInputStream(inputFile);
		ByteArrayOutputStream bos = (ByteArrayOutputStream) pdfGenerator.generate(is, resourceLoc);
  
 ```
 
Usage5:
 
Input is list of BufferedImage and generate Byte Array containing the resulting PDF.
 
 ```
		@Autowired
 		PDFGenerator pdfGenerator;
 		
		BufferedImage bufferedImage;
		BufferedImage bufferedImage2;

		List<BufferedImage> bufferedImages = new ArrayList<>();
	
		URL url = PDFGeneratorTest.class.getResource("/Change.jpg");
		URL url2 = PDFGeneratorTest.class.getResource("/nelsonmandela1-2x.jpg");

		bufferedImage = ImageIO.read(url);
		bufferedImages.add(bufferedImage);
		bufferedImage2 = ImageIO.read(url2);
		bufferedImages.add(bufferedImage2);
		
		byte[] data = pdfGenerator.asPDF(bufferedImages);
  
 ```

Usage6:
 
Input is list of URL of pdf files and generate Byte Array containing the resulting PDF.
 
```
		@Autowired
 		PDFGenerator pdfGenerator;

		List<URL> pdfFiles = new ArrayList<URL>(Arrays.asList(PDFGeneratorTest.class.getResource("/sample.pdf"),
				PDFGeneratorTest.class.getResource("/pdf-sample.pdf")));
		byte[] byteArray = pdfGenerator.mergePDF(pdfFiles);
  
 ```





