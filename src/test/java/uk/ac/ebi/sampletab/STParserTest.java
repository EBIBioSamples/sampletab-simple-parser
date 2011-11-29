package uk.ac.ebi.sampletab;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import static java.lang.System.out;

public class STParserTest
{
	@Test
	public void testSTParser () throws Exception
	{
		Submission submission = STParser3.readST ( new File ( 
			System.getProperty ( "basedir" ) + "/target/test-classes/derivation.sampletab.csv"
		));
		
		out.println ( "SUBMISSION ID: " + submission.getID () );
		out.println ( "SUBMISSION VALUE: " + submission.getValue () );
		out.println (  getContainerObjectText ( submission ) );
		
		out.println ( "\nSAMPLES" );
		for ( List<Sample> block: submission.getSampleBlocks () )
			for  ( Sample sample: block )
			{
				out.println ( "SAMPLE ID: " + sample.getID () );
				out.println (  getAnnotatableText ( sample ) );
				out.print ( "SAMPLE's UPSTREAM SAMPLES:" );
				
				String sep ="";
				for ( Sample upstream: sample.getDeriverFromSamples () ) {
					out.print ( sep + upstream.getID () ); sep = ", ";
				}
				out.println ();
				
				out.println ();
		}
	}

	private String getContainerObjectText ( ContainerObject o )
	{
		StringBuilder result = new StringBuilder ( getAnnotatableText ( o ) );

		for (  String key: o.getAttachedClasses () ) 
		{
			result.append ( "----------- Attached objects of type " + key + ": -------------" ).append ( "\n" );
			for ( WellDefinedObject attachedObj: o.getAttachedObjects ( key ) )
				result.append ( getAnnotatableText ( attachedObj ) );
			result.append ( "-----------------------------------------" );
		}
		
		return result.toString ();
	}

	private String getAnnotatableText ( AnnotatedObject o )
	{
		StringBuilder result = new StringBuilder ();
		for ( Attribute a: o.getAnnotations () ) 
			result.append ( getAttributeText ( a, 0 ) );
		
		return result.toString ();
	}
	
	private String getAttributeText ( Attribute a, int indent )
	{
		return getAttributeText ( a, indent, new HashSet<Attribute> () );
	}

	private String getAttributeText ( Attribute a, int indent, Set<Attribute> doneAttrs )
	{
		if ( doneAttrs.contains ( a ) ) return "";
		doneAttrs.add ( a );
		
		StringBuilder result = new StringBuilder ();
		String indentStr = StringUtils.repeat ( "  ", indent );
		result.append ( indentStr + "Attribute ID: " ).append ( a.getID () ).append ( "\n" );
		result.append ( indentStr + "Attribute Name: " ).append ( a.getName () ).append ( "\n" );
		result.append ( indentStr + "Attribute Value: " ).append ( a.getValue () ).append ( "\n" );
		
		indent++;
//out.println ( result );

		result.append ( indentStr + "Attribute Values: " ).append ( "\n" );
		for ( Attribute val: a.getValues () )
			result.append ( getAttributeText ( val, indent, doneAttrs ) );
		
		Collection<Attribute> anns = a.getAnnotations ();
		
		if ( anns != null )
		{
			result.append ( indentStr + "Attribute Annotations: " ).append ( "\n" );
			for ( Attribute ann: a.getAnnotations () )
				result.append ( getAttributeText ( ann, indent, doneAttrs ) );
		}
		
		return result.toString ();
	}

}
