package com.a65apps.changelog.presentation.mustache

import com.a65apps.changelog.domain.entity.Changelog
import com.a65apps.changelog.domain.repository.IoRepository
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.io.Reader
import java.io.Writer

object MustacheRendererSpecification : Spek({
    Feature("рендерер должен уметь отрисовывать данные по шаблону") {
        val templateName = "changelog.mustache"
        val data = Changelog(
            title = "changelog"
        )

        Scenario("отрисовка данных") {
            val template = mockk<Reader>(relaxed = true)
            val output = mockk<Writer>(relaxed = true)
            val ioRepository = mockk<IoRepository>()
            val factory = mockk<MustacheFactory>(relaxed = true)
            val mustache = mockk<Mustache>(relaxed = true)

            Given("на вход отрисовщика подаются данные из файловой системы, " +
                    "шаблон: $templateName и выходной файл") {
                every { ioRepository.template } returns template
                every { ioRepository.templateName } returns templateName
                every { ioRepository.output } returns output
                every { factory.compile(template, templateName) } returns mustache
            }

            When("происходит отрисовка данных: $data") {
                MustacheRenderer(factory, ioRepository).render(data)
            }

            Then("должны скомпилировать из шаблона Mustache") {
                verify { factory.compile(template, templateName) }
            }
            And("должны получить выходной поток записи") {
                verify { ioRepository.output }
            }
            And("должны запустить отрисовку данных $data в выходной поток") {
                verify { mustache.execute(output, data) }
            }
            And("должны применить записанные данные в выходной поток") {
                verify { output.flush() }
            }
        }
    }
})
