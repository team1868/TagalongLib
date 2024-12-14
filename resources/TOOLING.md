## Recommended Tools
We us a variety of open source tools and custom scripts to build, improve, collaborate, and manage our code projects. The tooling we use is mostly inspired by industrial open source projects and best practices. For the most part, these tools are accessible and free to FRC teams with valuable application to almost any code project.

#### GitHub

Many of these features require the non-free version of GitHub. Non-profits may apply and receive the first tier of paid services for free, though receiving these benefits can take several months. This tier includes a fairly generous monthly allocation of cloud compute resources.

- Pull Requests (PR) and Code Reviews
  - Absolutely essential to a functional programming team and developing good code that is understood by more than a single person. Students and mentors review code, improving code quality and developing the essential skill of reading other people's code.
- Issues
  - GitHub issues are not just for issues in the code base! We use it to track features, tasks, and todo lists too! Issues are an essential feature for any open source project and professional workflow.
- Projects
  - Used as a way to sort, organize, view issues. This helps us track the progress and tasks for each sprint, subdivision, and programmer. It is a great tool on top of issues and helps identify what or who is falling behind and may need help.
- Protected Branches with CODEOWNERS file
  - Block directly pushing to the default or `main` branch and ensures programmers are using pull requests. Given the sensitivity of some configuration files, certain PRs require reviews from certain mentors or leaders.
- Delete branch on merge
  - Deletes the branch on github after the PR has been merged, minimizing the number of old branches that are unused.
- Continuous Integration and Continuous Delivery (CICD)
  - Compiles and runs any unit tests in the code. Even the most simple CICD that compiles the code is a requirement when working with new programmers who are unfamiliar with github.
- Workflows
  - Scripts and small applications triggered by specific actions like PRs against specified branches. For example, automatic code formatting, PR tagging, and issue labeling.


#### Pre-commit
Capable of running custom scripted checks and enforces rules like code formatting whenever a user tries to commit changes locally. Helps reinforce some of the GitHub protections and features while making programmers more aware of their mistakes before they enter the github black box.

- `end-of-file-fixer`
  - Requires a newline at end of file
- `trailing-whitespace`
  - Removes trailing whitespace on any line of code
- `no-commit-to-branch`
  - Blocks commits to local branches matching the specified names
- `clang-format`
  - Code formatter that is ran on all modified files

These tools help minimize merge conflicts and simplify code review and collaboration efforts.

#### Code formatting
We use [clang-format](https://clang.llvm.org/docs/ClangFormat.html) partly because it integrates well with our other tools. However many FRC teams use spotless integrated into their gradle builds. Regardless of which code formatter you use, we highly encourage the use of the format on save feature.

##### Spotless Integration
1. Add the spotless plugin to the `plugins` section of the `build.gradle` file. Usually on the first line.
```gradle
    id 'com.diffplug.spotless' version '6.12.0'
```
2. Add a spotless code formatting option to gradle by adding the following to the **end** of the `build.gradle` file.
```gradle
// Configure Spotless code formatter
project.compileJava.dependsOn(spotlessApply)
spotless {
    java {
        target fileTree('.') {
            include '**/*.java'
            exclude '**/build/**', '**/build-*/**', 'venv/**'
        }
        toggleOffOn()
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
```
3. Run the formatter in the command line
```bash
./gradlew spotlessApply
```

##### Format on Save
1. Add the VS Code plugin for your preferred formatter
    - Clang-Format (xaver.clang-format)
    - Spotless Gradle (richardwillis.vscode-spotless-gradle)
2. Add the following to `.vscode/settings.json`. Double check and remove any duplicate entries. Swap `xaver.clang-format` for `richardwillis.vscode-spotless-gradle` if using spotless instead of clang-format.
```
  "editor.defaultFormatter": "xaver.clang-format",
  "editor.formatOnSave": true,
  "[java]": {
    "editor.defaultFormatter": "xaver.clang-format",
    "editor.formatOnSave": true
  },
```

#### Style guide
We have a style guide that provides guidance and examples for the programming team.

- Code
  - Variable, argument, function, and class naming
  - Code structure
  - Code comment expectations
  - Directory and file structure
  - Preferred syntax for loops and logic shortcuts
  - Basic WPILib VS Code usage and IDE shortcuts
- GitHub
  - Branch, issue, and PR naming
  - Pull Request guidelines and expectations
  - Review process and responsibilities
  - Branch etiquette
  - Issue labeling process
  - Basic `git` commands and examples
  - Basic GitHub usage

#### VS Code plugins
In addition to the extensions installed by default in the WPILib VS Code, we use the following plugins listed in order of importance and value to our team.
1. Live Share
  - Remote work, pair programming, debugging build issues only seen by one person, etc. This has been an essential tool for the team since the pandemic and continues to be.
2. Clang-Format (xaver.clang-format)
    - VS Code formatter support for `clang-format`
    - Spotless Gradle (richardwillis.vscode-spotless-gradle) is the `spotless` equivalent
3. Code Spell Checker (streetsidesoftware.code-spell-checker)
    - Spell checks the code, an additional information stream that helps spot code typos and other hard to debug mistakes
4. Test Runner for Java
    - List unit tests and tracks results. Simplifies the process to run singular tests and visually verifying that all tests are passing.
