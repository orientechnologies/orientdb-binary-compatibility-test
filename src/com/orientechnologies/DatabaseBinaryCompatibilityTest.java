package com.orientechnologies;

import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseCompare;
import com.orientechnologies.orient.core.db.tool.ODatabaseImport;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>
 * @since 2/27/14
 */
public class DatabaseBinaryCompatibilityTest {
	public static void main(String[] args) {
		try {
			final String wrongFormat = "Wrong format of arguments required " + DatabaseBinaryCompatibilityTest.class.getName() + " <original db path> <exported db path> <path for new db>";
			if (args.length < 3) {
				System.err.println(wrongFormat);
				System.exit(-1);
			}


			final String url = args[0];
			final String exportPath = args[1];
			final String newDBurl = args[2];


			final ODatabaseDocumentTx database = new ODatabaseDocumentTx(newDBurl);
			if (database.exists()) {
				database.open("admin", "admin");
				database.drop();
			}

			database.create();
			ODatabaseImport dbImport = new ODatabaseImport(database, exportPath, new OCommandOutputListener() {
				public void onMessage(String message) {
					System.out.println(message);
				}
			});

			dbImport.setPreserveRids(true);
			dbImport.setDeleteRIDMapping(false);
			dbImport.importDatabase();

			dbImport.close();
			database.close();

			final ODatabaseCompare databaseCompare = new ODatabaseCompare(url, newDBurl, "admin", "admin", new OCommandOutputListener() {
				public void onMessage(String message) {
					System.out.println(message);
				}
			});


			final boolean dbsAreEqual = databaseCompare.compare();
			if (!dbsAreEqual)
				System.exit(1);
		} catch (Exception e) {
			System.err.println("Error during execution of binary compatibility test");
			e.printStackTrace();
			System.exit(-1);
		}
	}
}