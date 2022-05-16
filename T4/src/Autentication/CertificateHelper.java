//JOÃO MARCELLO BESSA RODRIGUES - 1720539
//RAFAEL RAMOS FELICIANO - 1521772

package Autentication;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.cert.CertificateEncodingException;
import java.util.Base64;

public class CertificateHelper {
	
	static X509Certificate x509Certificate;
	
	public CertificateHelper( String certificatePath ) throws Exception {	
		Path f = Paths.get(certificatePath);
		if( !f.toFile().exists() ) {
			throw new Exception("Arquivo Certificado não existe");
		}
		
		try {
			byte[] certificateAsByte = Files.readAllBytes(Paths.get(certificatePath));
			CertificateFactory certiFactory = CertificateFactory.getInstance("X.509");
			InputStream certiAsInputStream = new ByteArrayInputStream(certificateAsByte);
			x509Certificate = (X509Certificate) certiFactory.generateCertificate(certiAsInputStream);
		} catch( Exception e ) {
			throw new Exception("Problema no certificado");
		}
	}
	
	public String convertToPem() throws CertificateEncodingException {
	    String BEGIN_CERT = "-----BEGIN CERTIFICATE-----"; 
	    String END_CERT = "-----END CERTIFICATE-----";
	    String LINE_SEPARATOR = "\n";

	    final Base64.Encoder encoder = Base64.getMimeEncoder(64, LINE_SEPARATOR.getBytes());
	    final byte[] rawCrtText = x509Certificate.getEncoded();
	    final String encodedCertText = new String(encoder.encode(rawCrtText));
	    final String prettified_cert = BEGIN_CERT + LINE_SEPARATOR + encodedCertText + LINE_SEPARATOR + END_CERT;
	    return prettified_cert;
	  }
	
	public String getCertificateEmail() {
		return getCertificateField("EMAILADDRESS");
	}
	
	public String getCertificateEmissor() {
		return getIssuerCertificateField("CN");
	}
	
	public BigInteger getCertificateSerialNumber() {
		return x509Certificate.getSerialNumber();
	}
	
	public String getCertificateSignatureType() {
		return x509Certificate.getSigAlgName();
	}
	
	public String getCertificateUserName() {
		return getCertificateField("CN");
	}

	public Date getCertificateValidate() {
		return x509Certificate.getNotAfter();
	}
	
	public int getCertificateVersion() {
		return x509Certificate.getVersion();
	}
	
	private String getCertificateField( String field ) {
		// EX: CN=(.*?),.*"
		String regex = ".*" + field + "=([^,]*).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(x509Certificate.getSubjectDN().toString());
        if( matcher.matches() ) {
        	return matcher.group(1);
        }
        return null;
	}
	
	private String getIssuerCertificateField( String field ) {
		String regex = ".*" + field + "=([^,]*).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(x509Certificate.getIssuerDN().toString());
        if( matcher.matches() ) {
        	return matcher.group(1);
        }
        return null;
	}
}
