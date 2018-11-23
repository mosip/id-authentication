## kernel-pdfgenerator-itext
This application can be used to convert plain/html template to PDF. It contains several methods that accept template as Input Stream,File and String.

[Background & Design](../../design/kernel/kernel-pdfgenerator.md)
 
### Api Documentation

 [API Documentation <TBA>](TBA)
 
 ```
 mvn javadoc:javadoc

 ```

** Input template can be send  in the following format:**

1. InputStream
2. String
3. File



**The response will be generated PDF file as OutputStream or generated PDF file as given output file path and and file name.**
 
**If there is any error which occurs while generate pdf, it will be thrown as Exception.**

######Exceptions to be handled while using this functionality:

1. PDFGeneratorException
2. IOException 

#####Maven Dependency 
 ```
 <dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-pdfgenerator-itext</artifactId>
			<version>${project.version}</version>
 </dependency>
 ```
##### Usage Sample
 
 Usage1:
 
##### Input processed Template as inputStream and generate OutputStream containing the resulting PDF;
 
 ```
@Autowired
 PDFGenerator pdfGenerator;
 
     ClassLoader classLoader = getClass().getClassLoader();
	  String inputFile = classLoader.getResource("csshtml.html").getFile();
	  InputStream is = new FileInputStream(inputFile);
     OutputStream os = pdfGenerator.generate(inputStream);
  
 ```

 Usage2:
 
##### Input processed Template as String and generate OutputStream containing the resulting PDF;
 
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
 
##### Generate PDF take processed template as file, output file path and output file name and generate the resulting PDF in given output path with the given output file name.
 
 
 ```
 @Autowired
 PDFGenerator pdfGenerator;
 
        String outputPath = System.getProperty("user.dir");
		ClassLoader classLoader = getClass().getClassLoader();
		String inputFile = classLoader.getResource("textcontant.txt").getFile();
		String generatedPdfFileName = "textcontant";
        pdfGenerator.generate(templatePath,outpuFilePath,outputFileName);
       
 ```








