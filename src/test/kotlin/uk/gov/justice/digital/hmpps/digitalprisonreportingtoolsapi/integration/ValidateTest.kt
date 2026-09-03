package uk.gov.justice.digital.hmpps.digitalprisonreportingtoolsapi.integration

import com.google.gson.Gson
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
import uk.gov.justice.digital.hmpps.digitalprisonreportinglib.data.model.ProductDefinition

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = ["spring.main.allow-bean-definition-overriding=true"])
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ValidateTest {
  @Autowired
  private lateinit var kotlinxJson: Json

  @Autowired
  private lateinit var gson: Gson

  @Test
  fun `should deserialize properly`() {
    val json = """
      {
        "id": "id1",
        "name": "p1",
        "description": "d1",
        "metadata": {
          "author": "DPR",
          "owner": "DPR",
          "version": "0.1"
        },
        "datasource": [
          {
            "id": "ds1",
            "name": "dsn1"
          }
        ],
        "dataset": [
          {
            "id": "dsi1",
            "name": "dsin1",
            "datasource": "ds1",
            "query": "select 1",
            "schema": {
              "field": [
                {
                  "name": "n1",
                  "type": "string",
                  "display": "n1"
                },
                {
                  "name": "n2",
                  "type": "int",
                  "display": "n2"
                },
                {
                  "name": "n3",
                  "type": "int",
                  "display": "n3"
                },
                {
                  "name": "n4",
                  "type": "int",
                  "display": "n4"
                }
              ]
            }
          },
          {
            "id": "ds2",
            "name": "dsn2",
            "datasource": "ds2",
            "query": "select 2",
            "schema": {
              "field": [
                {
                  "name": "sf1",
                  "type": "timestamp",
                  "display": "sf1d",
                  "filter": {
                    "index": 0,
                    "interactive": "true",
                    "type": "daterange",
                    "default": "today(-3, months) - today()"
                  }
                },
                {
                  "name": "sf2",
                  "type": "int",
                  "display": "sf2",
                  "formula": "make_url('/dpr/request-report/report/rep1/repvar2/filters?',${'$'}{sf3},TRUE)"
                },
                {
                  "name": "sf3",
                  "type": "int",
                  "display": "sf3"
                },
                {
                  "name": "sf4",
                  "type": "int",
                  "display": "sf4"
                },
                {
                  "name": "sf5",
                  "type": "int",
                  "display": "sf5"
                },
                {
                  "name": "sf6",
                  "type": "int",
                  "display": "sf6"
                },
                {
                  "name": "sf7",
                  "type": "int",
                  "display": "sf7"
                }
              ]
            }
          },
          {
            "id": "ds3",
            "name": "dsn3",
            "datasource": "ds3",
            "query": "select 3",
            "schema": {
              "field": [
                {
                  "name": "sf1",
                  "type": "string",
                  "display": "sf1"
                }
              ]
            }
          }
        ],
        "policy": [
          {
            "id": "lao",
            "type": "lao",
            "rule": [
              {
                "effect": "permit",
                "condition": []
              }
            ]
          },
          {
            "id": "access",
            "type": "access",
            "rule": [
              {
                "effect": "permit",
                "condition": [
                  {
                    "match": ["${'$'}{role}", "SOME_ROLE"]
                  }
                ]
              }
            ]
          }
        ],
        "report": [
          {
            "id": "rep1",
            "name": "rep1",
            "description": "rep1",
            "classification": "Official",
            "version": "1.0.0",
            "render": "HTML",
            "dataset": "${'$'}ref:ds1",
            "feature": [
              {
                "type": "print"
              }
            ],
            "metadata": {
              "hints": ["interactive"]
            },
            "specification": {
              "template": "list",
              "field": [
                {
                  "name": "${'$'}ref:sf1",
                  "display": "ID",
                  "formula": "",
                  "visible": "true",
                  "sortable": true,
                  "defaultsort": true
                },
                {
                  "name": "${'$'}ref:sf2",
                  "display": "Created",
                  "formula": "",
                  "visible": "true",
                  "sortable": true,
                  "defaultsort": false,
                  "filter": {
                    "index": 1,
                    "type": "daterange"
                  }
                },
                {
                  "name": "${'$'}ref:sf3",
                  "display": "Status",
                  "visible": "true",
                  "sortable": false,
                  "defaultsort": false,
                  "filter": {
                    "index": 1,
                    "type": "multiselect",
                    "interactive": "true",
                    "staticoptions": [
                      {
                        "name": "DRAFT",
                        "display": "Draft"
                      },
                      {
                        "name": "AGREED",
                        "display": "Agreed"
                      },
                      {
                        "name": "DO_NOT_AGREE",
                        "display": "Do not agree"
                      },
                      {
                        "name": "COULD_NOT_ANSWER",
                        "display": "Could not answer"
                      },
                      {
                        "name": "UPDATED_COULD_NOT_AGREE",
                        "display": "Updated Not Agree"
                      },
                      {
                        "name": "UPDATED_AGREED",
                        "display": "Updated Agreed"
                      }
                    ]
                  }
                }
              ]
            }
          }
        ],
        "dashboard": [
          {
            "id": "d1",
            "name": "d1",
            "description": "d1",
            "dataset": "ds2",
            "section": [
              {
                "id": "s1",
                "display": "s1",
                "visualisation": [
                  {
                    "id": "pc1",
                    "type": "list",
                    "display": "v1",
                    "option": {
                      "showLatest": false
                    },
                    "column": {
                      "key": [
                        {
                          "id": "${'$'}ref:ts",
                          "display": "Date"
                        }
                      ],
                      "measure": [
                        {
                          "id": "${'$'}ref:ts",
                          "display": "Date"
                        },
                        {
                          "id": "${'$'}ref:link",
                          "display": "Count"
                        },
                        {
                          "id": "${'$'}ref:draft_count",
                          "display": "# No agreement"
                        },
                        {
                          "id": "${'$'}ref:agreed_pct",
                          "display": "Agreed %"
                        },
                        {
                          "id": "${'$'}ref:not_agreed_pct",
                          "display": "Not agreed %"
                        },
                        {
                          "id": "${'$'}ref:could_not_answer_pct",
                          "display": "Could not answer %"
                        },
                        {
                          "id": "${'$'}ref:updated_agreed_count_pct",
                          "display": "Updated Agreed %"
                        },
                        {
                          "id": "${'$'}ref:updated_could_not_agree_pct",
                          "display": "Updated count not agree %"
                        },
                        {
                          "id": "${'$'}ref:total_to_date",
                          "display": "Total to date"
                        }
                      ],
                      "expectNull": false
                    }
                  }
                ]
              }
            ]
          }
        ]
      }
    """.trimIndent()
    val pd = gson.fromJson(json, ProductDefinition::class.java)
    kotlinxJson.decodeFromString<ProductDefinition>(json)
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
                              "type": "string",
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
    assertThat(exception.message!!).contains("dataset[0].schema.field[0]")
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
