package com.ppolivka.gitlabprojects.api;

import com.ppolivka.gitlabprojects.api.dto.NamespaceDto;
import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.http.GitlabHTTPRequestor;
import org.gitlab.api.models.GitlabBranch;
import org.gitlab.api.models.GitlabMergeRequest;
import org.gitlab.api.models.GitlabNamespace;
import org.gitlab.api.models.GitlabNote;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;
import org.gitlab.api.models.GitlabUser;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Facade aroud GitLab REST API
 *
 * @author ppolivka
 * @since 9.10.2015
 */
public class ApiFacade {

    GitlabAPI api;

    public ApiFacade() {
    }

    public ApiFacade(String host, String key) {
        reload(host, key);
    }

    public boolean reload(String host, String key) {
        if (host != null && key != null && !host.isEmpty() && !key.isEmpty()) {
            api = GitlabAPI.connect(host, key, TokenType.PRIVATE_TOKEN, AuthMethod.URL_PARAMETER);
            api.ignoreCertificateErrors(true);
            return true;
        }
        return false;
    }

    public GitlabSession getSession() throws IOException {
        return api.getCurrentSession();
    }

    private void checkApi() throws IOException {
        if (api == null) {
            throw new IOException("please, configure plugin settings");
        }
    }

    public List<NamespaceDto> getNamespaces() throws IOException {
        return api.retrieve().getAll("/namespaces", NamespaceDto[].class);
    }

    public List<GitlabMergeRequest> getMergeRequests(GitlabProject project) throws IOException {
        return api.getOpenMergeRequests(project);
    }

    public List<GitlabNote> getMergeRequestComments(GitlabMergeRequest mergeRequest) throws IOException {
        return api.getNotes(mergeRequest);
    }

    public void addComment(GitlabMergeRequest mergeRequest, String body) throws IOException {
        api.createNote(mergeRequest, body);
    }

    public GitlabMergeRequest createMergeRequest(GitlabProject project, GitlabUser assignee, String from, String to, String title, String description, boolean removeSourceBranch) throws IOException {
        String tailUrl = "/projects/" + project.getId() + "/merge_requests";
        GitlabHTTPRequestor requestor = api.dispatch()
                .with("source_branch", from)
                .with("target_branch", to)
                .with("title", title)
                .with("description", description);
        if(removeSourceBranch) {
            requestor.with("remove_source_branch", true);
        }
        if (assignee != null) {
            requestor.with("assignee_id", assignee.getId());
        }

        return requestor.to(tailUrl, GitlabMergeRequest.class);
    }

    public void acceptMergeRequest(GitlabProject project, GitlabMergeRequest mergeRequest) throws IOException {
        api.acceptMergeRequest(project, mergeRequest.getIid(), null);
    }

    public void changeAssignee(GitlabProject project, GitlabMergeRequest mergeRequest, GitlabUser user) throws IOException {
        api.updateMergeRequest(project.getId(), mergeRequest.getIid(), null, user.getId(), null, null, null, null);
    }

    public GitlabProject createProject(String name, String visibilityLevel, boolean isPublic, NamespaceDto namespace, String description) throws IOException {
        return api.createProject(
                name,
                namespace != null && namespace.getId() != 0 ? namespace.getId() : null,
                description,
                null,
                null,
                null,
                null,
                null,
                isPublic,
                visibilityLevel,
                null
        );
    }

    public GitlabProject getProject(Integer id) throws IOException {
        return api.getProject(id);
    }

    public List<GitlabBranch> loadProjectBranches(GitlabProject gitlabProject) throws IOException {
        return api.getBranches(gitlabProject);
    }

    public Collection<GitlabProject> getProjects() throws Throwable {
        checkApi();

        SortedSet<GitlabProject> result = new TreeSet<>(new Comparator<GitlabProject>() {
            @Override
            public int compare(GitlabProject o1, GitlabProject o2) {
                GitlabNamespace namespace1 = o1.getNamespace();
                String n1 = namespace1 != null ? namespace1.getName().toLowerCase() : "Default";
                GitlabNamespace namespace2 = o2.getNamespace();
                String n2 = namespace2 != null ? namespace2.getName().toLowerCase() : "Default";

                int compareNamespace = n1.compareTo(n2);
                return compareNamespace != 0 ? compareNamespace : o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });

        List<GitlabProject> projects;
        try {
            projects = api.getMembershipProjects();
        } catch (Throwable e) {
            projects = Collections.emptyList();
        }
        projects = projects.stream().filter(project -> !Boolean.TRUE.equals(project.isArchived())).collect(Collectors.toList());
        result.addAll(projects);

        return result;
    }

    public Collection<GitlabUser> searchUsers(GitlabProject project, String text) throws IOException {
        checkApi();
        List<GitlabUser> users = new ArrayList<>();
        if (text != null) {
            String tailUrl = GitlabProject.URL + "/" + project.getId() + "/users" + "?search=" + URLEncoder.encode(text, "UTF-8");
            GitlabUser[] response = api.retrieve().to(tailUrl, GitlabUser[].class);
            users = Arrays.asList(response);
        }
        return users;
    }

    public GitlabUser getCurrentUser() throws IOException {
        checkApi();
        return api.getUser();
    }
}
