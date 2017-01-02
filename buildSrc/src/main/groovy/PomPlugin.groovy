import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

/**
 * Pom plugin "fixes" maven-publish plugin pom generation.
 * <p>
 * Plugin adds simplified pom configuration extension. Using pom closure in build new sections could be added
 * to resulted pom. If multiple maven publications configured, pom modification will be applied to all of them.
 *
 * @author Vyacheslav Rusakov
 * @since 04.11.2015
 */
@CompileStatic(TypeCheckingMode.SKIP)
class PomPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // extensions mechanism not used because we need free closure for pom xml modification
        project.convention.plugins.pom = new PomConvention()
        activatePomModifications(project)
    }

    private void activatePomModifications(Project project) {
        project.afterEvaluate {
            PublishingExtension publishing = project.publishing
            // apply to all configured maven publications
            publishing.publications.withType(MavenPublication) {
                pom.withXml {
                    Node pomXml = asNode()
                    applyUserPom(project, pomXml)
                }
            }
        }
    }

    private void applyUserPom(Project project, Node pomXml) {
        PomConvention pomExt = project.convention.plugins.pom
        if (pomExt.config) {
            XmlMerger.mergePom(pomXml, pomExt.config)
        }
        pomExt.xmlModifier?.call(pomXml)
        // apply defaults if required
        if (!pomXml.name) {
            pomXml.appendNode('name', project.name)
        }
        if (project.description && !pomXml.description) {
            pomXml.appendNode('description', project.description)
        }
    }
}
