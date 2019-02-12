package io.mosip.registrationprocessor.print.stage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registrationprocessor.print.stage.resend.PrintStage;

/**
 * @author M1048399
 *
 */
public class PrintStageApplication 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("io.mosip.registrationprocessor.print.stage.config");
		
		ctx.refresh();
		PrintStage printStage = ctx.getBean(PrintStage.class);
		printStage.deployVerticle();
    }
}
