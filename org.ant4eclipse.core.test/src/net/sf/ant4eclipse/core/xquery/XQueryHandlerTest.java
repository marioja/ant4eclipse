/*******************************************************************************
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License Version 1.0
 * (the "License"). You may not use this file except in compliance with the
 * License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is Ant4Eclipse. The Initial Developers of the Original Code
 * are Nils Hartmann and Gerd Wuetherich.
 * 
 * Contributor(s): Daniel Kasmeroglu
 ******************************************************************************/
package net.sf.ant4eclipse.core.xquery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.InputStream;

import net.sf.ant4eclipse.core.CoreExceptionCode;
import net.sf.ant4eclipse.core.exception.Ant4EclipseException;

import org.junit.Test;

/**
 * A simple handler used to verify the functionality of the XQueryHandler class.
 */
public class XQueryHandlerTest {

  /**
   * Runs the tests.
   * 
   * @throws Exception
   *           Testing failed for some reason.
   */
  @Test
  public void testQueries() throws Exception {

    InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream("net/sf/ant4eclipse/lang/xquery/data.xml");

    XQueryHandler handler = new XQueryHandler();

    // retrieve the attributes 'name' and 'age'. the resulting arrays will have the
    // same length because their length depends on the number of 'element' elements.
    XQuery query1 = handler.createQuery("//database/element/@name");
    XQuery query2 = handler.createQuery("//database/element/@age");

    // get the data values of 'file' and 'length' elements. the arrays are having
    // different lengths.
    XQuery query3 = handler.createQuery("//database/folder/file");
    XQuery query4 = handler.createQuery("//database/folder/length");

    // this query generates a data entry for the 'length' element in case it
    // is missing within a 'folder' element. this way the array length can
    // be guaruanteed for multiple data entries.
    XQuery query5 = handler.createQuery("//database/{folder}/length");

    // similar but using a condition.
    XQuery query6 = handler.createQuery("//database/{folder[@name='device:B']}/length");

    // wildcard examples.
    XQuery query7 = handler.createQuery("//*/element/@name");
    XQuery query8 = handler.createQuery("//*/*/file");

    // get an attribute of an indexed element.
    XQuery query9 = handler.createQuery("//database/element[1]/@name");

    // function query. this one creates numbers indicating the occurrence of 'entry'
    // elements within 'group' elements.
    XQuery query10 = handler.createQuery("//database/group/entry[count()]");

    // run the SAX parser while evaluating the queries
    XQueryHandler.queryInputStream(inputStream, handler);

    String[] values1 = query1.getResult();
    assertNotNull(values1);
    assertEquals(3, values1.length);
    assertEquals("Pjotr", values1[0]);
    assertEquals("Frijda", values1[1]);
    assertEquals("Hugo", values1[2]);

    String[] values2 = query2.getResult();
    assertNotNull(values2);
    assertEquals(3, values2.length);
    assertEquals(null, values2[0]);
    assertEquals("27", values2[1]);
    assertEquals(null, values2[2]);

    String[] values3 = query3.getResult();
    assertNotNull(values3);
    assertEquals(2, values3.length);
    assertEquals("diary.txt", values3[0]);
    assertEquals("public.pgp", values3[1]);

    String[] values4 = query4.getResult();
    assertNotNull(values4);
    assertEquals(1, values4.length);
    assertEquals("77618", values4[0]);

    String[] values5 = query5.getResult();
    assertNotNull(values5);
    assertEquals(2, values5.length);
    assertEquals("77618", values5[0]);
    assertEquals(null, values5[1]);

    String[] values6 = query6.getResult();
    assertNotNull(values6);
    assertEquals(1, values6.length);
    assertEquals(null, values6[0]);

    String[] values7 = query7.getResult();
    assertNotNull(values7);
    assertEquals(3, values7.length);
    assertEquals("Pjotr", values7[0]);
    assertEquals("Frijda", values7[1]);
    assertEquals("Hugo", values7[2]);

    String[] values8 = query8.getResult();
    assertNotNull(values8);
    assertEquals(2, values8.length);
    assertEquals("diary.txt", values8[0]);
    assertEquals("public.pgp", values8[1]);

    String[] values9 = query9.getResult();
    assertNotNull(values9);
    assertEquals(1, values9.length);
    assertEquals("Frijda", values9[0]);

    String[] values10 = query10.getResult();
    assertNotNull(values10);
    assertEquals(2, values10.length);
    assertEquals("2", values10[0]);
    assertEquals("4", values10[1]);
  }

  @Test
  public void testInvalidQueries() {
    XQueryHandler handler = new XQueryHandler();

    try {
      // retrieve the attributes 'name' and 'age'. the resulting arrays will have the
      // same length because their length depends on the number of 'element' elements.
      handler.createQuery("/database/element/@name");
    } catch (Ant4EclipseException e) {
      assertSame(e.getExceptionCode(), CoreExceptionCode.X_QUERY_INVALID_QUERY_EXCEPTION);
      assertEquals("Invalid x-query '/database/element/@name': Query needs to starts with two slashes !", e
          .getMessage());
    }
  }
} /* ENDCLASS */
