package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.eclipse.EclipsePlugin

class EclipsePlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.plugins.apply(EclipsePlugin)

		project.eclipse {
			classpath {
				file {
					whenMerged {
						entries
							.findAll { entry -> entry instanceof org.gradle.plugins.ide.eclipse.model.ProjectDependency }
							.each {
								it.entryAttributes['without_test_code'] = 'false'
								it.entryAttributes['test'] = 'false'
							}
					}
				}
			}
		}
	}

}
