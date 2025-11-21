package renderer;

import entities.application.Application;
import entities.posting.InternshipPosting;
import entities.users.CompanyRep;

/**
 * Class to print an application
 */
public final class ApplicationRenderer {
    /**
     * Empty constructor to prevent initialisation
     */
    private ApplicationRenderer() {}
    /**
     * Print application for student
     */
    public static String studentRenderBox(Application a, InternshipPosting p, CompanyRep rep) {
        String[] lines = {
                RenderUtil.fmt("Application ID", a.getId()),
                RenderUtil.fmt("Application Status", a.getStatus()),
                RenderUtil.fmt("Application Date", a.getAppliedOn()),
                RenderUtil.fmt("Latest Changes Update", a.getAppliedOn()),
                "",
                RenderUtil.fmt("Posting Title", p.getTitle()),
                RenderUtil.fmt("Description", p.getDescription()),
                RenderUtil.fmt("Posting ID", p.getId()),
                RenderUtil.fmt("Company Name", rep.getCompanyName()),
                RenderUtil.fmt("Company Rep Email", rep.getUsername()),
                RenderUtil.fmt("Internship Level", p.getLevel()),
                RenderUtil.fmt("Preferred Major", p.getMajor()),
                RenderUtil.fmt("Application Opening Date", p.getOpenDate()),
                RenderUtil.fmt("Application Closing Date", p.getCloseDate()),
                RenderUtil.fmt("Posting Status", p.getStatus()),
                RenderUtil.fmt("Posting Visibility", p.getVisibility()),
                RenderUtil.fmt("Slots", p.getSlots()),
                RenderUtil.fmt("Confirmed", String.format("%d / %d", p.getConfirmed(), p.getSlots())),
                RenderUtil.fmt("Has capacity", p.hasCapacity() ? "YES" : "NO")
        };
        return RenderUtil.boxRender(lines);
    }
    /**
     * Print application for company rep
     */
    public static String repRenderBox(Application a) {
        String[] lines = {
                RenderUtil.fmt("Application ID", a.getId()),
                RenderUtil.fmt("Application Status", a.getStatus()),
                RenderUtil.fmt("Application Date", a.getAppliedOn()),
                RenderUtil.fmt("Latest Changes Update", a.getAppliedOn()),
        };
        return RenderUtil.boxRender(lines);
    }


}
