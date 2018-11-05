## kernel-templatemanager-freemarker

 
 1- [Background & Design](../../design/kernel/kernel-templatemanager.md)
 

 2- [API Documentation <TBA>](TBA)
 
 ```
 mvn javadoc:javadoc

 ```
 
 3- Usage Sample
 
 Usage1:
 
 ```
TemplateManager templateManager = new TemplateConfigureBuilder().build();

InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.ftl");
InputStream data = templateManager.merge(is, valueMap);
StringWriter writer = new StringWriter();
IOUtils.copy(data, writer, "UTF-8");
 
 ```

 Usage2:
 
 ```
<TBA>
 
 ```








