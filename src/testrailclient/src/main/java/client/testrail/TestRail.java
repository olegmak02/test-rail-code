/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Kunal Shah
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package client.testrail;

import java.util.Date;
import client.testrail.internal.BooleanToIntSerializer;
import client.testrail.internal.ListToCsvSerializer;
import client.testrail.model.Case;
import client.testrail.model.CaseField;
import client.testrail.model.CaseType;
import client.testrail.model.Configuration;
import client.testrail.model.Page;
import client.testrail.model.Priority;
import client.testrail.model.Project;
import client.testrail.model.Section;
import client.testrail.model.Suite;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * Client for Test Rail API. Configure and use it to create requests for the API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/start">TestRail API v2 Documentation</a>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class TestRail {

    @Getter(value = AccessLevel.MODULE)
    @Accessors(fluent = false)
    private final TestRailConfig config;

    /**
     * Get a builder to build an instance of {@code TestRail}.
     *
     * @param endPoint the URL end point where your TestRail is hosted, for e.g. https://example.com/testrail
     * @param username the username of the TestRail user on behalf of which the communication with TestRail will happen
     * @param password the password of the same TestRail user
     * @return a builder to build {@code TestRail} instance
     */
    public static Builder builder(@NonNull final String endPoint, @NonNull final String username, @NonNull final String password) {
        return new Builder(endPoint, username, password);
    }

    /**
     * An accessor for creating requests for "Projects".
     *
     * @return a request factory
     */
    public Projects projects() {
        return new Projects();
    }

    /**
     * An accessor for creating requests for "Cases".
     *
     * @return a request factory
     */
    public Cases cases() {
        return new Cases();
    }

    /**
     * An accessor for creating requests for "Case Fields".
     *
     * @return a request factory
     */
    public CaseFields caseFields() {
        return new CaseFields();
    }

    /**
     * An accessor for creating requests for "Case Types".
     *
     * @return a request factory
     */
    public CaseTypes caseTypes() {
        return new CaseTypes();
    }

    /**
     * An accessor for creating requests for "Configurations".
     *
     * @return a request factory
     */
    public Configurations configurations() {
        return new Configurations();
    }

    /**
     * An accessor for creating requests for "Sections".
     *
     * @return a request factory
     */
    public Sections sections() {
        return new Sections();
    }

    /**
     * An accessor for creating requests for "Suites".
     *
     * @return a request factory
     */
    public Suites suites() {
        return new Suites();
    }

    /**
     * An accessor for creating requests for "Priorities".
     *
     * @return a request factory
     */
    public Priorities priorities() {
        return new Priorities();
    }

    /**
     * Builder for {@code TestRail}.
     */
    public static class Builder {

        private static final String DEFAULT_BASE_API_PATH = "index.php?/api/v2/";

        private final String endPoint;
        private final String username;
        private final String password;
        private String apiPath;
        private String applicationName;

        /**
         * @param endPoint the URL end point where your TestRail is hosted, for e.g. https://example.com/testrail
         * @param username the username of the TestRail user on behalf of which the communication with TestRail will happen
         * @param password the password of the same TestRail user
         */
        private Builder(final String endPoint, final String username, final String password) {
            String sanitizedEndPoint = endPoint.trim();
            if (!sanitizedEndPoint.endsWith("/")) {
                sanitizedEndPoint = sanitizedEndPoint + "/";
            }
            this.endPoint = sanitizedEndPoint;
            this.username = username;
            this.password = password;
            apiPath = DEFAULT_BASE_API_PATH;
        }

        /**
         * Set the URL path of your TestRail API. Useful to override the default API path of standard TestRail deployments.
         *
         * @param apiPath the URL path of your TestRail API
         * @return this for chaining
         * @throws NullPointerException if apiPath is null
         */
        public Builder apiPath(@NonNull final String apiPath) {
            String sanitizedApiPath = apiPath.trim();
            if (sanitizedApiPath.startsWith("/")) {
                sanitizedApiPath = sanitizedApiPath.substring(1);
            }
            if (!sanitizedApiPath.endsWith("/")) {
                sanitizedApiPath = sanitizedApiPath + "/";
            }
            this.apiPath = sanitizedApiPath;
            return this;
        }

        /**
         * Set the name of the application which will communicate with TestRail.
         *
         * @param applicationName name of the application
         * @return this for chaining
         * @throws NullPointerException if applicationName is null
         */
        public Builder applicationName(@NonNull final String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        /**
         * Build an instance of {@code TestRail}.
         *
         * @return a new instance
         */
        public TestRail build() {
            return new TestRail(new TestRailConfig(endPoint + apiPath, username, password, applicationName));
        }
    }

    /**
     * Request factories for "Projects".
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Projects {

        /**
         * Returns an existing project.
         *
         * @param projectId the ID of the project
         * @return the request
         * @throws IllegalArgumentException if projectId is not positive
         */
        public Get get(final int projectId) {
            checkArgument(projectId > 0, "projectId should be positive");
            return new Get(projectId);
        }

        /**
         * Returns the list of available projects.
         *
         * @return the request
         */
        public List list() {
            return new List();
        }

        /**
         * Creates a new project.
         *
         * @param project the project to be added
         * @return the request
         * @throws NullPointerException if project is null
         */
        public Add add(@NonNull Project project) {
            return new Add(project);
        }

        /**
         * Updates an existing project. Partial updates are supported, i.e. you can set and update specific fields only.
         *
         * @param project the project to be updated
         * @return the request
         * @throws NullPointerException if project is null
         */
        public Update update(@NonNull Project project) {
            return new Update(project);
        }

        /**
         * Deletes an existing project.
         *
         * @param projectId the ID of the project to be deleted
         * @return the request
         * @throws IllegalArgumentException if projectId is not positive
         */
        public Delete delete(final int projectId) {
            checkArgument(projectId > 0, "projectId should be positive");
            return new Delete(projectId);
        }

        public class Get extends Request<Project> {
            private static final String REST_PATH = "get_project/";

            private Get(int projectId) {
                super(config, Method.GET, REST_PATH + projectId, Project.class);
            }
        }

        @Getter
        @Setter
        public class List extends Request<java.util.List<Project>> {
            private static final String REST_PATH = "get_projects";

            @JsonView(List.class)
            @JsonSerialize(using = BooleanToIntSerializer.class)
            private Boolean isCompleted;

            private List() {
                super(config, Method.GET, REST_PATH, new TypeReference<java.util.List<Project>>() {
                }, new TypeReference<Page<java.util.List<Project>>>(){});
            }
        }

        public class Add extends Request<Project> {
            private static final String REST_PATH = "add_project";

            private final Project project;

            private Add(@NonNull Project project) {
                super(config, Method.POST, REST_PATH, Project.class);
                this.project = project;
            }

            @Override
            protected Object getContent() {
                return project;
            }

        }

        public class Update extends Request<Project> {
            private static final String REST_PATH = "update_project/";

            private final Project project;

            private Update(@NonNull Project project) {
                super(config, Method.POST, REST_PATH + project.getId(), Project.class);
                this.project = project;
            }

            @Override
            protected Object getContent() {
                return project;
            }

        }

        public class Delete extends Request<Void> {
            private static final String REST_PATH = "delete_project/";

            private Delete(int projectId) {
                super(config, Method.POST, REST_PATH + projectId, Void.class);
            }

        }
    }

    /**
     * Request factories for "Cases".
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Cases {

        /**
         * Returns an existing test case.
         * <p>The custom case fields configured in TestRail can be fetched using {@link CaseFields#list()} request.
         * The reason for not fetching this during execution of this request is to allow you to cache the list on your end to prevent an extra call on every execution.</p>
         *
         * @param testCaseId the ID of the test case
         * @param caseFields the custom case fields configured in TestRail to get type information for custom fields in the test case returned
         * @return the request
         * @throws IllegalArgumentException if testCaseId is not positive
         * @throws NullPointerException     if caseFields is null
         */
        public Get get(final int testCaseId, @NonNull java.util.List<CaseField> caseFields) {
            checkArgument(testCaseId > 0, "testCaseId should be positive");
            return new Get(testCaseId, caseFields);
        }

        /**
         * Returns the list of available test cases.
         * <p>The custom case fields configured in TestRail can be fetched using {@link CaseFields#list()} request.
         * The reason for not fetching this during execution of this request is to allow you to cache the list on your end to prevent an extra call on every execution.</p>
         *
         * @param projectId  the ID of the project which is operating in a single suite mode
         * @param caseFields the custom case fields configured in TestRail to get type information for custom fields in the test cases returned
         * @return the request
         * @throws IllegalArgumentException if projectId is not positive
         * @throws NullPointerException     if caseFields is null
         */
        public List list(final int projectId, @NonNull java.util.List<CaseField> caseFields) {
            checkArgument(projectId > 0, "projectId should be positive");
            return new List(projectId, caseFields);
        }

        /**
         * Returns the list of available test cases.
         * <p>The custom case fields configured in TestRail can be fetched using {@link CaseFields#list()} request.
         * The reason for not fetching this during execution of this request is to allow you to cache the list on your end to prevent an extra call on every execution.</p>
         *
         * @param projectId  the ID of the project
         * @param suiteId    the ID of the suite
         * @param caseFields the custom case fields configured in TestRail to get type information for custom fields in the test cases returned
         * @return the request
         * @throws IllegalArgumentException if any argument is not positive
         * @throws NullPointerException     if caseFields is null
         */
        public List list(final int projectId, final int suiteId, @NonNull java.util.List<CaseField> caseFields) {
            checkArgument(projectId > 0, "projectId should be positive");
            checkArgument(suiteId > 0, "suiteId should be positive");
            return new List(projectId, suiteId, caseFields);
        }

        /**
         * Creates a new test case.
         * <p>The custom case fields configured in TestRail can be fetched using {@link CaseFields#list()} request.
         * The reason for not fetching this during execution of this request is to allow you to cache the list on your end to prevent an extra call on every execution.</p>
         *
         * @param sectionId  the ID of the section to add the test case to
         * @param testCase   the test case to be added
         * @param caseFields the custom case fields configured in TestRail to get type information for custom fields in the test case returned
         * @return the request
         * @throws IllegalArgumentException if sectionId is not positive
         * @throws NullPointerException     if any other argument is null
         */
        public Add add(final int sectionId, @NonNull Case testCase, @NonNull java.util.List<CaseField> caseFields) {
            checkArgument(sectionId > 0, "projectId should be positive");
            return new Add(sectionId, testCase, caseFields);
        }

        /**
         * Updates an existing test case. Partial updates are supported, i.e. you can set and update specific fields only.
         * <p>The custom case fields configured in TestRail can be fetched using {@link CaseFields#list()} request.
         * The reason for not fetching this during execution of this request is to allow you to cache the list on your end to prevent an extra call on every execution.</p>
         *
         * @param testCase   the test case to be updated
         * @param caseFields the custom case fields configured in TestRail to get type information for custom fields in the test case returned
         * @return the request
         * @throws NullPointerException if any argument is null
         */
        public Update update(@NonNull Case testCase, @NonNull java.util.List<CaseField> caseFields) {
            return new Update(testCase, caseFields);
        }

        /**
         * Deletes an existing test case.
         *
         * @param testCaseId the ID of the test case to be deleted
         * @return the request
         * @throws IllegalArgumentException if testCaseId is not positive
         */
        public Delete delete(final int testCaseId) {
            checkArgument(testCaseId > 0, "testCaseId should be positive");
            return new Delete(testCaseId);
        }

        public class Get extends Request<Case> {
            private static final String REST_PATH = "get_case/";

            private final java.util.List<CaseField> caseFields;

            private Get(int testCaseId, java.util.List<CaseField> caseFields) {
                super(config, Method.GET, REST_PATH + testCaseId, Case.class);
                this.caseFields = caseFields;
            }

            @Override
            protected Object getSupplementForDeserialization() {
                return caseFields;
            }
        }

        @Getter
        @Setter
        @Accessors(fluent = true)
        public class List extends Request<java.util.List<Case>> {
            private static final String REST_PATH = "get_cases/%s&suite_id=%s";
            private final java.util.List<CaseField> caseFields;
            @JsonView(List.class)
            private Integer sectionId;
            @JsonView(List.class)
            private Date createdAfter;
            @JsonView(List.class)
            private Date createdBefore;
            @JsonView(List.class)
            @JsonSerialize(using = ListToCsvSerializer.class)
            private java.util.List<Integer> createdBy;
            @JsonView(List.class)
            @JsonSerialize(using = ListToCsvSerializer.class)
            private java.util.List<Integer> milestoneId;
            @JsonView(List.class)
            @JsonSerialize(using = ListToCsvSerializer.class)
            private java.util.List<Integer> priorityId;
            @JsonView(List.class)
            @JsonSerialize(using = ListToCsvSerializer.class)
            private java.util.List<Integer> typeId;
            @JsonView(List.class)
            private Date updatedAfter;
            @JsonView(List.class)
            private Date updatedBefore;
            @JsonView(List.class)
            @JsonSerialize(using = ListToCsvSerializer.class)
            private java.util.List<Integer> updatedBy;

            private List(int projectId, java.util.List<CaseField> caseFields) {
                super(config, Method.GET, String.format(REST_PATH, projectId, ""), new TypeReference<java.util.List<Case>>() {
                }, new TypeReference<Page<java.util.List<Case>>>(){});
                this.caseFields = caseFields;
            }

            private List(int projectId, int suiteId, java.util.List<CaseField> caseFields) {
                super(config, Method.GET, String.format(REST_PATH, projectId, suiteId), new TypeReference<java.util.List<Case>>() {
                }, new TypeReference<Page<java.util.List<Case>>>(){});
                this.caseFields = caseFields;
            }

            @Override
            protected Object getSupplementForDeserialization() {
                return caseFields;
            }

        }

        public class Add extends Request<Case> {
            private static final String REST_PATH = "add_case/";

            private final Case testCase;
            private final java.util.List<CaseField> caseFields;

            private Add(int sectionId, Case testCase, java.util.List<CaseField> caseFields) {
                super(config, Method.POST, REST_PATH + sectionId, Case.class);
                this.testCase = testCase;
                this.caseFields = caseFields;
            }

            @Override
            protected Object getContent() {
                return testCase;
            }

            @Override
            protected Object getSupplementForDeserialization() {
                return caseFields;
            }

        }

        public class Update extends Request<Case> {
            private static final String REST_PATH = "update_case/";

            private final Case testCase;
            private final java.util.List<CaseField> caseFields;

            private Update(Case testCase, java.util.List<CaseField> caseFields) {
                super(config, Method.POST, REST_PATH + testCase.getId(), Case.class);
                this.testCase = testCase;
                this.caseFields = caseFields;
            }

            @Override
            protected Object getContent() {
                return testCase;
            }

            @Override
            protected Object getSupplementForDeserialization() {
                return caseFields;
            }

        }

        public class Delete extends Request<Void> {
            private static final String REST_PATH = "delete_case/";

            private Delete(int testCaseId) {
                super(config, Method.POST, REST_PATH + testCaseId, Void.class);
            }
        }
    }

    /**
     * Request factories for "Case Fields".
     */
    @NoArgsConstructor
    public class CaseFields {

        /**
         * Returns a list of available test case custom fields.
         *
         * @return the request
         */
        public List list() {
            return new List();
        }

        public class List extends Request<java.util.List<CaseField>> {
            private static final String REST_PATH = "get_case_fields";

            private List() {
                super(config, Method.GET, REST_PATH, new TypeReference<java.util.List<CaseField>>() {
                }, new TypeReference<Page<java.util.List<CaseField>>>(){});
            }
        }
    }

    /**
     * Request factories for "Case Types".
     */
    @NoArgsConstructor
    public class CaseTypes {

        /**
         * Returns a list of available case types.
         *
         * @return the request
         */
        public List list() {
            return new List();
        }

        public class List extends Request<java.util.List<CaseType>> {
            private static final String REST_PATH = "get_case_types";

            private List() {
                super(config, Method.GET, REST_PATH, new TypeReference<java.util.List<CaseType>>() {
                }, new TypeReference<Page<java.util.List<CaseType>>>(){});
            }
        }

    }

    /**
     * Request factories for "Configurations".
     */
    @NoArgsConstructor
    public class Configurations {

        /**
         * Returns a list of available configurations, grouped by configuration groups.
         *
         * @param projectId the ID of the project to get the configurations for
         * @return the request
         * @throws IllegalArgumentException if projectId is not positive
         */
        public List list(final int projectId) {
            checkArgument(projectId > 0, "projectId should be positive");
            return new List(projectId);
        }

        public class List extends Request<java.util.List<Configuration>> {
            private static final String REST_PATH = "get_configs/";

            private List(int projectId) {
                super(config, Method.GET, REST_PATH + projectId, new TypeReference<java.util.List<Configuration>>() {
                }, new TypeReference<Page<java.util.List<Configuration>>>(){});
            }

        }

    }

    /**
     * Request factories for "Priorities".
     */
    @NoArgsConstructor
    public class Priorities {

        /**
         * Returns a list of available priorities.
         *
         * @return the request
         */
        public List list() {
            return new List();
        }

        public class List extends Request<java.util.List<Priority>> {
            private static final String REST_PATH = "get_priorities";

            private List() {
                super(config, Method.GET, REST_PATH, new TypeReference<java.util.List<Priority>>() {
                }, new TypeReference<Page<java.util.List<Priority>>>(){});
            }
        }

    }

    /**
     * Request factories for "Sections".
     */
    @NoArgsConstructor
    public class Sections {

        /**
         * Returns an existing section.
         *
         * @param sectionId the ID of the section
         * @return the request
         * @throws IllegalArgumentException if sectionId is not positive
         */
        public Get get(final int sectionId) {
            checkArgument(sectionId > 0, "sectionId should be positive");
            return new Get(sectionId);
        }

        /**
         * Returns a list of sections for a project.
         *
         * @param projectId the ID of the project which is operating in a single suite mode
         * @return the request
         * @throws IllegalArgumentException if projectId is not positive
         */
        public List list(final int projectId) {
            checkArgument(projectId > 0, "projectId should be positive");
            return new List(projectId);
        }

        /**
         * Returns a list of sections for a project and test suite.
         *
         * @param projectId the ID of the project
         * @param suiteId   the ID of the suite
         * @return the request
         * @throws IllegalArgumentException if any argument is not positive
         */
        public List list(final int projectId, final int suiteId) {
            checkArgument(projectId > 0, "projectId should be positive");
            checkArgument(suiteId > 0, "suiteId should be positive");
            return new List(projectId, suiteId);
        }

        /**
         * Creates a new section.
         *
         * @param projectId the ID of the project to add the section to
         * @param section   the section to be added
         * @return the request
         * @throws NullPointerException if section is null
         */
        public Add add(final int projectId, @NonNull Section section) {
            checkArgument(projectId > 0, "projectId should be positive");
            return new Add(projectId, section);
        }

        /**
         * Updates an existing section. Partial updates are supported, i.e. you can set and update specific fields only.
         *
         * @param section the section to be updated
         * @return the request
         * @throws NullPointerException if section is null
         */
        public Update update(@NonNull Section section) {
            return new Update(section);
        }

        /**
         * Deletes an existing section.
         *
         * @param sectionId the ID of the section to be deleted
         * @return the request
         * @throws IllegalArgumentException if sectionId is not positive
         */
        public Delete delete(final int sectionId) {
            checkArgument(sectionId > 0, "sectionId should be positive");
            return new Delete(sectionId);
        }

        public class Get extends Request<Section> {
            private static final String REST_PATH = "get_section/";

            private Get(int sectionId) {
                super(config, Method.GET, REST_PATH + sectionId, Section.class);
            }

        }

        public class List extends Request<java.util.List<Section>> {
            private static final String REST_PATH = "get_sections/%s&suite_id=%s";

            private List(int projectId) {
                super(config, Method.GET, String.format(REST_PATH, projectId, ""), new TypeReference<java.util.List<Section>>() {
                }, new TypeReference<Page<java.util.List<Section>>>(){});
            }

            private List(int projectId, int suiteId) {
                super(config, Method.GET, String.format(REST_PATH, projectId, suiteId), new TypeReference<java.util.List<Section>>() {
                }, new TypeReference<Page<java.util.List<Section>>>(){});
            }
        }

        public class Add extends Request<Section> {
            private static final String REST_PATH = "add_section/";

            private final Section section;

            private Add(int projectId, Section section) {
                super(config, Method.POST, REST_PATH + projectId, Section.class);
                this.section = section;
            }

            @Override
            protected Object getContent() {
                return section;
            }
        }

        public class Update extends Request<Section> {
            private static final String REST_PATH = "update_section/";

            private final Section section;

            private Update(Section section) {
                super(config, Method.POST, REST_PATH + section.getId(), Section.class);
                this.section = section;
            }

            @Override
            protected Object getContent() {
                return section;
            }
        }

        public class Delete extends Request<Void> {
            private static final String REST_PATH = "delete_section/";

            private Delete(int sectionId) {
                super(config, Method.POST, REST_PATH + sectionId, Void.class);
            }
        }
    }

    /**
     * Request factories for "Suites".
     */
    @NoArgsConstructor
    public class Suites {

        /**
         * Returns an existing test suite.
         *
         * @param suiteId the ID of the test suite
         * @return the request
         * @throws IllegalArgumentException if suiteId is not positive
         */
        public Get get(final int suiteId) {
            checkArgument(suiteId > 0, "suiteId should be positive");
            return new Get(suiteId);
        }

        /**
         * Returns a list of test suites for a project.
         *
         * @param projectId the ID of the project to get the test suites for
         * @return the request
         * @throws IllegalArgumentException if projectId is not positive
         */
        public List list(final int projectId) {
            checkArgument(projectId > 0, "projectId should be positive");
            return new List(projectId);
        }

        /**
         * Creates a new test suite.
         *
         * @param projectId the ID of the project to add the test suite to
         * @param suite     the test suite to be added
         * @return the request
         * @throws IllegalArgumentException if projectId is not positive
         * @throws NullPointerException     if suite is null
         */
        public Add add(final int projectId, @NonNull Suite suite) {
            checkArgument(projectId > 0, "projectId should be positive");
            return new Add(projectId, suite);
        }

        /**
         * Updates an existing test suite. Partial updates are supported, i.e. you can set and update specific fields only.
         *
         * @param suite the test suite to be updated
         * @return the request
         * @throws NullPointerException if suite is null
         */
        public Update update(@NonNull Suite suite) {
            return new Update(suite);
        }

        /**
         * Deletes an existing test suite.
         *
         * @param suiteId the ID of the test suite to be deleted
         * @return the request
         * @throws IllegalArgumentException if suiteId is not positive
         */
        public Delete delete(final int suiteId) {
            checkArgument(suiteId > 0, "suiteId should be positive");
            return new Delete(suiteId);
        }

        public class Get extends Request<Suite> {
            private static final String REST_PATH = "get_suite/";

            private Get(int suiteId) {
                super(config, Method.GET, REST_PATH + suiteId, Suite.class);
            }
        }

        public class List extends Request<java.util.List<Suite>> {
            private static final String REST_PATH = "get_suites/";

            private List(int projectId) {
                super(config, Method.GET, REST_PATH + projectId, new TypeReference<java.util.List<Suite>>() {
                }, new TypeReference<Page<java.util.List<Suite>>>(){});
            }
        }

        public class Add extends Request<Suite> {
            private static final String REST_PATH = "add_suite/";

            private final Suite suite;

            private Add(final int projectId, Suite suite) {
                super(config, Method.POST, REST_PATH + projectId, Suite.class);
                this.suite = suite;
            }

            @Override
            protected Object getContent() {
                return suite;
            }
        }

        public class Update extends Request<Suite> {
            private static final String REST_PATH = "update_suite/";

            private final Suite suite;

            private Update(Suite suite) {
                super(config, Method.POST, REST_PATH + suite.getId(), Suite.class);
                this.suite = suite;
            }

            @Override
            protected Object getContent() {
                return suite;
            }
        }

        public class Delete extends Request<Void> {
            private static final String REST_PATH = "delete_suite/";

            private Delete(int suiteId) {
                super(config, Method.POST, REST_PATH + suiteId, Void.class);
            }
        }

    }
}
