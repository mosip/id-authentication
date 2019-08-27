**Design - Language Support**

**Background**

The registration client supports two languages, a primary language in which all pages of the application are rendered, and a secondary language in which select pages such as demographic details are also rendered for convenience of the individual. French and Arabic are the default primary and secondary languages, which are driven by an admin config and can be setup by the admin as required. Transliteration from the primary to secondary language is supported for user entered text fields.  

**Configuration**
  The configuration for primary and secondary language should be derived from global param configuration, which is defined at the config server and the same should be downloaded to the client application through the 'config sync' service.  
  
  
**Design**
  **Translation:**
  The multilingual feature would be achieved in the UI layer using the language specific resource bundle. For each language  the respective resource bundle should be created and same thing should be loaded during load of the application. 
    
  All the lables, warnings, informations and error messages are stored in language specific resource bundles. The values are converted into unicode format while storing into the bundle. whenever displaying the screen, UTF-8 format would be applied to display the right font and character in the screen. The UTF-8 format should be maintained across the application, that would support capture, display and storing the text in right language.
  
     - new FXMLLoader(url, ResourceBundle) - Pass resource bundle object based on the language.
  
  **Transliteration:**  
   To achieve this functionality, the third party library (ICU4J) should be used to covert the user typed language to secondary language. As the user type in the text, it would be passed to the library and that would return the text in target language (which is also passed as input to the library). It uses the both the languages unicode to convert it from one language to another format.        
    
     - The transliteration library is embeded in kernel component [kernel-transliteration-icu4j] and that should be used.