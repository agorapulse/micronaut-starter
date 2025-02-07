/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.starter.feature.other;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.starter.application.ApplicationType;
import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.build.gradle.GradlePlugin;
import io.micronaut.starter.feature.Category;
import io.micronaut.starter.feature.DefaultFeature;
import io.micronaut.starter.feature.Feature;
import io.micronaut.starter.options.Options;
import jakarta.inject.Singleton;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

/**
 * Adds a shaded JAR feature.
 */
@Singleton
public class ShadePlugin implements DefaultFeature {

    @Override
    public boolean shouldApply(
            ApplicationType applicationType,
            Options options,
            Set<Feature> selectedFeatures) {
        // maybe should not apply if JIB is selected
        return true;
    }

    @NonNull
    @Override
    public String getName() {
        return "shade";
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean supports(ApplicationType applicationType) {
        return true;
    }

    @Override
    public String getTitle() {
        return "Fat/Shaded JAR Support";
    }

    @Override
    public String getDescription() {
        return "Adds the ability to build a Fat/Shaded JAR";
    }

    @Override
    public String getCategory() {
        return Category.PACKAGING;
    }

    @Override
    public void apply(GeneratorContext generatorContext) {
        if (generatorContext.getBuildTool().isGradle()) {
            generatorContext.addHelpLink("Shadow Gradle Plugin", "https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow");
            GradlePlugin.Builder builder = GradlePlugin.builder()
                    .id("com.github.johnrengelman.shadow")
                    .lookupArtifactId("shadow");

            if (generatorContext.getApplicationType().equals(ApplicationType.FUNCTION)) {
                builder.extension(outputStream -> {
                    String str = String.join("\n", Arrays.asList(
                            "tasks.named(\"assemble\") {",
                            "    dependsOn(\":shadowJar\")",
                            "}"
                    ));
                    outputStream.write(str.getBytes(StandardCharsets.UTF_8));
                });
            }
            generatorContext.addBuildPlugin(builder.build());

        }
    }
}
