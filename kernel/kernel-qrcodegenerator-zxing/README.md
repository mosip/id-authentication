## kernel-qrcodegenerator-zxing
[Background & Design](TBA)

**Api Documentation**

[API Documentation <TBA>](TBA)

```
mvn javadoc:javadoc
```

**Maven dependency**
  
 ```
    <dependency>
		<groupId>io.mosip.kernel</groupId>
		<artifactId>kernel-qrcodegenerator-zxing</artifactId>
		<version>${project.version}</version>
	</dependency>
 ```

**The inputs which have to be provided are:**
1. Data to be encoded in QR-code in string.
2. QR code version.


**The response will be *byte array* of QR Code PNG image** 

**If there is any error which occurs while generating QR code, it will be thrown as Exception.** 

**Exceptions to be handled while using this functionality:**
1. QrcodeGenerationException
2. NullPointerException
3. InvalidInputException
4. IOException

**Usage Sample**
  
*Usage:*
 
 ```
@Autowired
QrCodeGenerator<QrVersion> generator;
	
byte[] qrCodeData = generator.generateQrCode(dataToEncode,qrCodeVersion);
```
 
 *Output*
 
```
�PNGIHDR����g�IDATx��Y͊�����*�Ӏ��4�N����==���ަ��]kw}F]?):��k�� q�y���&�.�:V�� ,���l�� 3{.���G�@T%�Qd_��i�>g,oS���i-�Z��P��~��4�)�H�J�u�`�Z�X��3T*��Q)�yN��|=�YyxpF�#&-��왲��<| :o�݂��Ԧ�"�c�`����EXʙ��Z}��</ ��l�>���~bX��#O��>1v�Q��jh�Y��]O��m���=wL�X��$�&a/��!��U'���$��T���2T{��G��.�K�%��O �����C����cw<q����4��b�6y�C�^�ϖ�U����kH5�O Ş�j>w��ٍ��}����y���}c��*%L�e�=� X�ÓY�Z�-�TKH�`�Մv��s��G\�gw}��鎺b�t�f�-v�=��v�ces��ˏH���ГF�p�B�Qv҆G�a/ .�%�^,�GN4�����3:�Ljc6���`�> 8/ ���0�����_m9H菫� �Yk[w��ݓ-s�:/����ٖiL�,l�dI�� �fX#�vht�6�о����Lc�4�p�7D�m<p����-L��ٖ/�M�� x���3$��&��\`�a?��_�F���+�;���;��@��R��	񠂭��#��^ $��� ���k�fI� �h���䱇�^t�U�z� �fa��A���,���k�'�w��������ˬ���'�����ꌅ���5����|� Χ�����=y���'�潈'��V��a�E�� �)I1Ɯ��������	@_��9׉P�����=�o����V^��d�e�>�  \��f��Ұ�h7�=k��L�'��-�F��n�	{ Q �L,�`9ύ�Z!�r2������� E{{`��ھ��1[Ԅ[x`8Ψ�i��S�F�'�Gb� p�Ͼ�u�䴙\(9�쿣�(�`�|�Ns_-Z��ka_ ױ68� �C���ȆI�'@�9r�^�w\s�ar@��.~��s3�m����ۯu�?0�Zp6�DX�'y�?�� l�� �\�	��I���'� ׁ�{���m	h����� ,��w�[FQxn��ay\bO "�����(^[&�����O@@�|"��v����`��!~HPC|����copP���G�y��|m�L��Yw��P���/��L���g��kBh3��^ ���o^�~�DjP$l�_�>|�~:�^�PX|�-��p��1���kAe�(���'�`���֎cq=�4�w_ b=��'�#tb[�����n~PPG�G�(�����SOy2�rbv�e�i�+w� �f�v������T>�b�'��ͲC��]�:�q���' +�-�sܱ���EȦ��' Z32`O���#_n�9����s7zP�0�"��|o��*� �ܭ��&�	�N7VW��O��Ѡ�X�����CE>`��t~�ӾW�!�W%f�p�"A7�SF�������1 ��f��b�K��?�o�=���(o|���]}zl���@�o`@���|ԟ]�(U1���°G�kycw^l�:Lvj���l���2yA;b�l����QͲ$z�M�~�{����}������� ����Y�EH�7��U*�7 �Y�1}6�{�B��0fD�Z0H��oPć��	��Q�#�,�ߨ�j"��?&)�J�����{m���K(_��D����$a�q� 6kN�ʢWy�co�o�?��	-n7S�ΰ�I��� �q�&�9�*��C�P���k� C��W����M�WF׏_ ���ns��b�2� [� ��_P�ݒ����ݳ���`ŹW<�8]�����,���0�Ζ����7�{Hv�~D�	PE*��m����k���Ê���� ����ɷч���*Uoy�{s�3�KMf!?����M��A�&���Bt�!U�>��(��~f�����:CP�l�' �h�d�!"�mZ?� +�%����=V�Y\��	8X߄�ܫ6F�N8���b1����G�T���{�������a8�BLǽ���h�6� �}�$�����$ Sm��G���R���ca0*���uj=rá�X�{��2u_2��#�O ml��<Ptl=��3P��>O b�{xF�I�D��0�>|� ��'����[� IEND�B`�
```
