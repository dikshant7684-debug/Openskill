package com.example

import com.example.data.model.Platform
import com.example.data.model.ResourceCategory
import com.example.data.repository.PreloadedCuratedData
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCuratedRepositoriesHaveAccurateData() {
    val repos = PreloadedCuratedData.sampleRepositories
    assertTrue("Curated repositories should not be empty", repos.isNotEmpty())

    val mcpRepo = repos.find { it.fullName == "modelcontextprotocol/servers" }
    assertNotNull("MCP servers repository should exist", mcpRepo)
    assertTrue("MCP repo should have skills", mcpRepo!!.hasSkills)
    assertTrue("MCP repo stars should be positive", mcpRepo.stars > 10000)
    assertEquals(Platform.GITHUB, mcpRepo.platform)
  }

  @Test
  fun testCuratedSkillsHaveMarkdownAndCommands() {
    val skills = PreloadedCuratedData.sampleSkills
    assertTrue("Sample skills should not be empty", skills.isNotEmpty())

    for (skill in skills) {
      assertNotNull("Skill name should not be null", skill.name)
      assertTrue("Skill should have instructions", skill.promptInstructions.isNotBlank())
      assertNotNull("Skill category should be set", skill.category)
    }
  }
}
