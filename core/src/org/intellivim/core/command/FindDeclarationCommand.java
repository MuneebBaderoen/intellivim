package org.intellivim.core.command;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.intellivim.Command;
import org.intellivim.ProjectCommand;
import org.intellivim.Required;
import org.intellivim.Result;
import org.intellivim.SimpleResult;
import org.intellivim.core.util.ProjectUtil;

/**
 * @author dhleong
 */
@Command("find_declaration")
public class FindDeclarationCommand extends ProjectCommand {

    public static class LocationResult {
        public final String file;
        public final int offset;

        LocationResult(PsiElement element) {
            final VirtualFile virtualFile = element.getContainingFile()
                    .getVirtualFile();

            file = virtualFile.getCanonicalPath();
            offset = element.getTextOffset();
        }
    }

    @Required String file;
    @Required int offset;

    public FindDeclarationCommand(Project project, String file, int offset) {
        super(project);

        this.file = file;
        this.offset = offset;
    }

    @Override
    public Result execute() {

        final PsiFile psiFile = ProjectUtil.getPsiFile(project, file);
        final PsiReference ref = psiFile.findReferenceAt(offset);
        final PsiElement lookup = ref.resolve();

        if (lookup == null)
            return SimpleResult.error("Definition not found.");

        return SimpleResult.success(new LocationResult(lookup));
    }
}