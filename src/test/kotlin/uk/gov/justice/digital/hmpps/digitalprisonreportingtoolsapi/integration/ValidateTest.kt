package uk.gov.justice.digital.hmpps.digitalprisonreportingtoolsapi.integration

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.common.model.DataDefinitionPath
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.common.model.LoadType
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.common.model.SortDirection
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.AggregateType
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Dashboard
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DashboardBucket
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DashboardChild
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DashboardOption
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DashboardSection
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DashboardVisualisation
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DashboardVisualisationColumn
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DashboardVisualisationColumns
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DashboardVisualisationType
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Dataset
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Datasource
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DatasourceConnection
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.DynamicFilterOption
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Feature
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.FeatureType
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.FilterDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.FilterType
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Granularity
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.MetaData
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.MultiphaseQuery
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Parameter
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ParameterType
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ProductDefinition
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.QuickFilter
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ReferenceType
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.RenderMethod
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Report
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ReportChild
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ReportField
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ReportFilter
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ReportMetadata
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ReportMetadataHint
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ReportSummary
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Schema
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.SchemaField
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Specification
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.SqlDialect
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.SummaryField
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.SummaryTemplate
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Template
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.UnitType
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ValueVisualisationColumn
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.Visible
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.WordWrap
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.policyengine.Condition
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.policyengine.Effect
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.policyengine.Policy
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.policyengine.PolicyType
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.policyengine.Rule

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = ["spring.main.allow-bean-definition-overriding=true"])
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ValidateTest {
  @Autowired
  private lateinit var kotlinxJson: Json

  @Test
  fun `should serialize properly`() {
    val dpd = ProductDefinition(
      id = "test",
      name = "test",
      description = "test",
      scheduled = false,
      metadata = MetaData("auth1", "0", "owner1", "ppse", "prof1"),
      path = DataDefinitionPath.ORPHANAGE,
      datasource = listOf(Datasource("id1", "name1", "db1", "cata1", DatasourceConnection.AWS_DATA_CATALOG, SqlDialect.ORACLE11g)),
      dataset = listOf(
        Dataset(
          "id1",
          "name1",
          "ds1",
          listOf(
            MultiphaseQuery(
              0,
              "ds1",
              "q1",
              listOf(
                Parameter(
                  0,
                  "name1",
                  ParameterType.DateTime,
                  FilterType.AutoCompleteMulti,
                  "",
                  true,
                  ReferenceType.WING,
                ),
              ),
            ),
          ),
          Schema(
            listOf(
              SchemaField(
                "name1",
                ParameterType.String,
                "disp1",
                FilterDefinition(
                  FilterType.Granulardaterange,
                  true,
                  "pat1",
                  emptyList(),
                  DynamicFilterOption(0, true, 10L, "ds1", "name1", "disp1"),
                  "def1",
                  "0",
                  "10",
                  true,
                  Granularity.DAILY,
                  QuickFilter.LAST_90_DAYS,
                  0,
                  0,
                  100,
                ),
                "form1",
              ),
            ),
          ),
          listOf(
            Parameter(
              0,
              "name1",
              ParameterType.String,
              FilterType.Caseloads,
              "",
              true,
              ReferenceType.WING,
            ),
          ),
          "sched1",
        ),
      ),
      listOf(
        Report(
          "id1",
          "name1",
          "description",
          "0",
          "ds1",
          RenderMethod.HTMLChild,
          "sched1",
          Specification(
            Template.RowSectionChild,
            listOf(
              ReportField(
                "rf1",
                "rfd1",
                WordWrap.None,
                FilterDefinition(FilterType.Granulardaterange),
                sortable = true,
                defaultSort = true,
                SortDirection.ASC,
                "form1",
                Visible.TRUE,
              ),
            ),
            listOf("foo"),
          ),
          listOf(mapOf("foo" to "bar")),
          "class1",
          listOf(Feature(FeatureType.PRINT)),
          listOf(
            ReportSummary(
              "sum1",
              "ds1",
              SummaryTemplate.SectionFooter,
              listOf(SummaryField("sf1", header = false, mergeRows = false)),
            ),
          ),
          ReportFilter("rf1", "q1", "id1", "desc1", "ds1", "0"),
          ReportMetadata(listOf(ReportMetadataHint.INTERACTIVE)),
          listOf(ReportChild("id1", listOf("rf1"))),
          false,
          LoadType.ASYNC,
        ),
      ),
      listOf(
        Policy(
          "p1",
          PolicyType.ROW_LEVEL,
          listOf("a1"),
          listOf(Rule(Effect.PERMIT, listOf(Condition(listOf("m1"), listOf("e1"))))),
        ),
      ),
      dashboard = listOf(
        Dashboard(
          "d1",
          "n1",
          "dd1",
          "ds1",
          listOf(
            DashboardSection(
              "s1",
              "ds1",
              "descds1",
              listOf(
                DashboardVisualisation(
                  "dsv1",
                  DashboardVisualisationType.BAR_TIMESERIES,
                  "disp1",
                  "descds1",
                  DashboardVisualisationColumns(
                    key = listOf(
                      DashboardVisualisationColumn(
                        "i1",
                        "d1",
                        AggregateType.SUM,
                        UnitType.PERCENTAGE,
                        true,
                        "axis",
                        false,
                      ),
                    ),
                    measure = listOf(
                      DashboardVisualisationColumn(
                        "1",
                        "d",
                        AggregateType.SUM,
                        UnitType.PERCENTAGE,
                        true,
                        "axis",
                        false,
                      ),
                    ),
                    filter = listOf(ValueVisualisationColumn("1", "anequals")),
                    false,
                  ),
                  DashboardOption(
                    true,
                    "bc",
                    listOf(DashboardBucket(0L, 10L, "#FFFFFF")),
                    showLatest = true,
                    columnsAsList = true,
                    horizontal = true,
                    xStacked = true,
                    yStacked = true,
                  ),
                ),
              ),
            ),
          ),
          ReportFilter("rf1", "q1", "id1", "desc1", "ds1", "0"),
          LoadType.ASYNC,
          listOf(DashboardChild("d1")),
        ),
      ),
    )

    val str = kotlinxJson.encodeToString(dpd)
    println("\n\n${str}\n\n")
    assertThat(kotlinxJson.decodeFromString<ProductDefinition>(str)).isEqualTo(dpd)
  }

  @Test
  fun `should fail when missing attribute`() {
    val json = """
      {
          "id": "success",
          "name": "test",
          "description": "test",
          
          "dataset": [
              {
                  "id": "success",
                  "datasource": "test",
                  "schema": {
                      "field": [
                          {
                              "name": "created_by",
                              "type": "date",
                              "display": "Created By User"
                          }
                      ]
                  }
              }
          ]
      }
    """.trimIndent()
    val exception = assertThrows<SerializationException> {
      kotlinxJson.decodeFromString<ProductDefinition>(json)
    }
    assertThat(exception.message!!).containsPattern("Field.*name.*required")
    assertThat(exception.message!!).contains("dataset[0]")
  }

  @Test
  fun `should fail with trailing comma`() {
    val json = """
      {
          "id": "success",
          "name": "test",
          "description": "test",
          
          "dataset": [
              {
                  "id": "success",
                  "name": "test",
                  "datasource": "test",
                  "schema": {
                      "field": [
                          {
                              "name": "created_by",
                              "display": "Created By User",
                          }
                      ]
                  }
              }
          ]
      }
    """.trimIndent()
    val exception = assertThrows<SerializationException> {
      kotlinxJson.decodeFromString<ProductDefinition>(json)
    }
    assertThat(exception.message!!).contains("Trailing comma")
    assertThat(exception.message!!).contains("dataset[0].schema.field[0].display")
  }

  @Test
  fun `should fail with unterminated object`() {
    val json = """
      {
          "id": "success",
          "name": "test",
          "description": "test",
          
          "dataset": [
              {
                  "id": "success",
                  "name": "test",
                  "datasource": "test",
                  "schema": {
                      "field": [
                          {
                              "name": "created_by",
                              "type": "date",
                              "display": "Created By User"
                          
                      ]
                  }
              }
          ]
      }
    """.trimIndent()
    val exception = assertThrows<SerializationException> {
      kotlinxJson.decodeFromString<ProductDefinition>(json)
    }
    assertThat(exception.message!!).contains("Expected end of the object")
    assertThat(exception.message!!).contains("dataset[0].schema.field[0]")
  }

  @Test
  fun `should fail with unterminated array`() {
    val json = """
      {
          "id": "success",
          "description": "test",
          "dataset": [
              {
                  "id": "success",
                  "name": "test",
                  "datasource": "test",
                  "schema": {
                      "field": [
                          {
                              "name": "created_by",
                              "type": "date",
                              "display": "Created By User"
                          }
                      
                  }
              }
          ]
      }
    """.trimIndent()
    val exception = assertThrows<SerializationException> {
      kotlinxJson.decodeFromString<ProductDefinition>(json)
    }
    assertThat(exception.message!!).contains("Expected end of the array")
    assertThat(exception.message!!).contains("dataset[0].schema.field")
  }

  @Test
  fun `should fail with unterminated string on value`() {
    val json = """
      {
          "id": "success",
          "name": "test",
          "description": "test",
          
          "dataset": [
              {
                  "id": "success",
                  "name": "test",
                  "datasource": "test,
                  "schema": {
                      "field": [
                          {
                              "name": "created_by",
                              "display": "Created By User"
                          }
                      ]
                  }
              }
          ]
      }
    """.trimIndent()
    val exception = assertThrows<SerializationException> {
      kotlinxJson.decodeFromString<ProductDefinition>(json)
    }
    assertThat(exception.message!!).contains("Expected quotation mark")
    assertThat(exception.message!!).contains("dataset[0].datasource")
  }

  @Test
  fun `should fail with missing colon`() {
    val json = """
      {
          "id": "success",
          "name": "test",
          "description": "test",
          
          "dataset" [
              {
                  "id": "success",
                  "name": "test",
                  "datasource": "test",
                  "schema": {
                      "field": [
                          {
                              "name": "created_by",
                              "display": "Created By User"
                          }
                      ]
                  }
              }
          ]
      }
    """.trimIndent()
    val exception = assertThrows<SerializationException> {
      kotlinxJson.decodeFromString<ProductDefinition>(json)
    }
    assertThat(exception.message!!).contains("Expected colon")
    assertThat(exception.message!!).contains("dataset")
  }
}
