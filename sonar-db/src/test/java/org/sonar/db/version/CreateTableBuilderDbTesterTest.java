/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db.version;

import java.util.Map;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.db.DbTester;
import org.sonar.db.dialect.Dialect;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.sonar.db.version.BigIntegerColumnDef.newBigIntegerColumnDefBuilder;
import static org.sonar.db.version.BlobColumnDef.newBlobColumnDefBuilder;
import static org.sonar.db.version.BooleanColumnDef.newBooleanColumnDefBuilder;
import static org.sonar.db.version.ClobColumnDef.newClobColumnDefBuilder;
import static org.sonar.db.version.CreateTableBuilder.ColumnFlag.AUTO_INCREMENT;
import static org.sonar.db.version.DecimalColumnDef.newDecimalColumnDefBuilder;
import static org.sonar.db.version.IntegerColumnDef.newIntegerColumnDefBuilder;
import static org.sonar.db.version.VarcharColumnDef.newVarcharColumnDefBuilder;

public class CreateTableBuilderDbTesterTest {
  @ClassRule
  public static final DbTester dbTester = DbTester.create(System2.INSTANCE);

  private Dialect dialect = dbTester.getDbClient().getDatabase().getDialect();
  private static int tableNameGenerator = 0;

  @Test
  public void create_no_primary_key_table() {
    newCreateTableBuilder()
      .addColumn(newBooleanColumnDefBuilder().setColumnName("bool_col_1").build())
      .addColumn(newBooleanColumnDefBuilder().setColumnName("bool_col_2").setIsNullable(false).build())
      .addColumn(newIntegerColumnDefBuilder().setColumnName("i_col_1").build())
      .addColumn(newIntegerColumnDefBuilder().setColumnName("i_col_2").setIsNullable(false).build())
      .addColumn(newBigIntegerColumnDefBuilder().setColumnName("bi_col_1").build())
      .addColumn(newBigIntegerColumnDefBuilder().setColumnName("bi_col_2").setIsNullable(false).build())
      .addColumn(newClobColumnDefBuilder().setColumnName("clob_col_1").build())
      .addColumn(newClobColumnDefBuilder().setColumnName("clob_col_2").setIsNullable(false).build())
      .addColumn(newDecimalColumnDefBuilder().setColumnName("dec_col_1").build())
      .addColumn(newDecimalColumnDefBuilder().setColumnName("dec_col_2").setIsNullable(false).build())
      .addColumn(new TinyIntColumnDef.Builder().setColumnName("tiny_col_1").build())
      .addColumn(new TinyIntColumnDef.Builder().setColumnName("tiny_col_2").setIsNullable(false).build())
      .addColumn(newVarcharColumnDefBuilder().setColumnName("varchar_col_1").setLimit(40).build())
      .addColumn(newVarcharColumnDefBuilder().setColumnName("varchar_col_2").setLimit(40).setIsNullable(false).build())
      .addColumn(newBlobColumnDefBuilder().setColumnName("blob_col_1").build())
      .addColumn(newBlobColumnDefBuilder().setColumnName("blob_col_2").setIsNullable(false).build())
      .build()
      .forEach(dbTester::executeDdl);
  }

  @Test
  public void create_single_column_primary_key_table() {
    newCreateTableBuilder()
      .addPkColumn(newBigIntegerColumnDefBuilder().setColumnName("bg_col_1").setIsNullable(false).build())
      .addColumn(newVarcharColumnDefBuilder().setColumnName("varchar_col_2").setLimit(40).setIsNullable(false).build())
      .build()
      .forEach(dbTester::executeDdl);
  }

  @Test
  public void create_multi_column_primary_key_table() {
    newCreateTableBuilder()
      .addPkColumn(newBigIntegerColumnDefBuilder().setColumnName("bg_col_1").setIsNullable(false).build())
      .addPkColumn(newBigIntegerColumnDefBuilder().setColumnName("bg_col_2").setIsNullable(false).build())
      .addColumn(newVarcharColumnDefBuilder().setColumnName("varchar_col_2").setLimit(40).setIsNullable(false).build())
      .build()
      .forEach(dbTester::executeDdl);
  }

  @Test
  public void create_autoincrement_notnullable_integer_primary_key_table() {
    String tableName = createTableName();
    new CreateTableBuilder(dialect, tableName)
      .addPkColumn(newIntegerColumnDefBuilder().setColumnName("id").setIsNullable(false).build(), AUTO_INCREMENT)
      .addColumn(valColumnDef())
      .build()
      .forEach(dbTester::executeDdl);

    verifyAutoIncrementIsWorking(tableName);
  }

  @Test
  public void create_autoincrement_notnullable_biginteger_primary_key_table() {
    String tableName = createTableName();
    new CreateTableBuilder(dialect, tableName)
      .addPkColumn(newBigIntegerColumnDefBuilder().setColumnName("id").setIsNullable(false).build(), AUTO_INCREMENT)
      .addColumn(valColumnDef())
      .build()
      .forEach(dbTester::executeDdl);

    verifyAutoIncrementIsWorking(tableName);
  }

  private static VarcharColumnDef valColumnDef() {
    return newVarcharColumnDefBuilder().setColumnName("val").setLimit(10).setIsNullable(false).build();
  }

  private void verifyAutoIncrementIsWorking(String tableName) {
    dbTester.executeInsert(tableName, "val", "toto");
    dbTester.commit();

    Map<String, Object> row = dbTester.selectFirst("select id as \"id\", val as \"val\" from " + tableName);
    assertThat(row.get("id")).isNotNull();
    assertThat(row.get("val")).isEqualTo("toto");
  }

  private CreateTableBuilder newCreateTableBuilder() {
    return new CreateTableBuilder(dialect, createTableName());
  }

  private static String createTableName() {
    return "table_" + tableNameGenerator++;
  }
}
