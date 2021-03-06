package org.intellivim.core.command.find;

import com.intellij.openapi.project.Project;
import org.intellivim.BaseTestCase;
import org.intellivim.SimpleResult;
import org.intellivim.core.util.ProjectUtil;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author dhleong
 */
public class FindImplementationsTest extends BaseTestCase {

    final String filePath = "src/org/intellivim/javaproject/SuperClass.java";

    @Override
    protected String getProjectPath() {
        return getProjectPath(JAVA_PROJECT);
    }

    public void testFindMethodImplementation() {
        final SimpleResult result = locateAt(170);
        assertSuccess(result);

        final List<LocationResult> list = result.getResult();
        assertThat(list)
                .isNotNull()
                .hasSize(1);

        final LocationResult loc = list.get(0);
        assertThat(loc.file).endsWith("ProperSubClass.java");
        assertThat(new File(loc.file)).exists();
        assertThat(loc.offset).isEqualTo(144);
    }

    SimpleResult locateAt(int offset) {
        final Project project = getProject();
        return execute(new FindImplementationsCommand(
                project,
                ProjectUtil.getPsiFile(project, filePath),
                offset));
    }
}

