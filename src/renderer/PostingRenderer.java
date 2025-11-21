package renderer;

import entities.posting.InternshipPosting;
import entities.users.CompanyRep;
/**
 * Class to print an internship posting
 */
public final class PostingRenderer {
    /**
     * Empty constructor to prevent initialisation
     */
    private PostingRenderer() {}
    /**
     * Print an internship posting
     */
    public static String renderBox(InternshipPosting p, CompanyRep rep) {
        String[] lines = {
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
}
