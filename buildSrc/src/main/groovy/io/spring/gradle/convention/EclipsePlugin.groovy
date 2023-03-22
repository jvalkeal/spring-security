package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.eclipse.EclipsePlugin

/**
 * Plugin to tweak .classpath settings so that eclipse/sts/vscode can
 * be imported cleanly.
 *
 * @author Janne Valkealahti
 */
class EclipsePlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.plugins.apply(EclipsePlugin)

		project.eclipse {
			classpath {
				file {
					whenMerged {
						// for aspects gradle eclipse integration wrongly creates lib entries for
						// aspects/build/classes/java/main and aspects/build/resources/main which
						// never has anything. eclipse/sts don't care, vscode language server
						// complains about missing folders. So remove those from a .classpath
						entries.removeAll {
							entry -> entry.kind == 'lib' && (entry.path.contains('aspects/build/classes/java/main') || entry.path.contains('aspects/build/resources/main'))
						}
						// there are cycles and then dependencies between projects main sources and
						// test sources. Looks like gradle eclipse integration is getting confused.
						// thus just relax rules so that projects always sees everything from
						// dependant project.
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
