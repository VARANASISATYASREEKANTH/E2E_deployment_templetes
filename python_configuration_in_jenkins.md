# Python Configuration in Jenkins

This guide will walk you through the steps to configure Python in Jenkins for running Python scripts, tests, and other tasks in your CI/CD pipelines.

---

## Prerequisites

1. **Jenkins Installed**:
   Ensure Jenkins is installed and accessible. You can download Jenkins from [here](https://www.jenkins.io/).

2. **Python Installed**:
   Python must be installed on the system where Jenkins is running. You can download Python from [here](https://www.python.org/downloads/).

3. **Jenkins User Permissions**:
   The Jenkins user should have permissions to access Python and execute commands.

4. **Python Environment Manager** (Optional):
   Tools like `venv` or `virtualenv` are recommended for isolating dependencies.

---

## Steps to Configure Python in Jenkins

### 1. Verify Python Installation

Ensure Python is installed and accessible from the command line.

```bash
python3 --version
```

If the command outputs the Python version, Python is installed correctly.

### 2. Install Necessary Python Packages

Install any required Python packages globally or in a virtual environment.

```bash
pip install <package_name>
```

For example:

```bash
pip install pytest requests
```

### 3. Configure Jenkins to Use Python

#### 3.1 Install Jenkins Plugins

- Navigate to `Manage Jenkins` > `Manage Plugins`.
- Install the following plugins:
  - **Pipeline** (for scripted and declarative pipelines)
  - **ShiningPanda** (optional, for managing Python virtual environments)

#### 3.2 Add Python to Global Tool Configuration

1. Navigate to `Manage Jenkins` > `Global Tool Configuration`.
2. Under the **Python** section:
   - Click `Add Python`.
   - Provide a name (e.g., `Python3`).
   - Specify the Python executable path (e.g., `/usr/bin/python3`).
   - Save the configuration.

#### 3.3 Use Python in a Jenkins Pipeline

##### Example: Declarative Pipeline

```groovy
pipeline {
    agent any

    environment {
        PYTHON_PATH = '/usr/bin/python3' // Adjust as needed
    }

    stages {
        stage('Setup') {
            steps {
                sh "${PYTHON_PATH} --version"
                sh "${PYTHON_PATH} -m pip install -r requirements.txt"
            }
        }

        stage('Run Tests') {
            steps {
                sh "${PYTHON_PATH} -m pytest tests/"
            }
        }
    }
}
```

##### Example: Scripted Pipeline

```groovy
node {
    def pythonPath = '/usr/bin/python3' // Adjust as needed

    stage('Setup') {
        sh "${pythonPath} --version"
        sh "${pythonPath} -m pip install -r requirements.txt"
    }

    stage('Run Tests') {
        sh "${pythonPath} -m pytest tests/"
    }
}
```

---

## Best Practices

1. **Use Virtual Environments:**
   Create and activate a virtual environment in the pipeline to isolate dependencies:

   ```groovy
   sh "${PYTHON_PATH} -m venv venv"
   sh "source venv/bin/activate"
   ```

2. **Cache Dependencies:**
   Cache Python packages to speed up builds:

   ```bash
   pip install --cache-dir=.cache <package_name>
   ```

3. **Test Locally:**
   Validate your Python scripts and pipelines locally before committing to Jenkins.

4. **Integrate Linting:**
   Add linting tools like `flake8` or `pylint` to your pipeline for code quality checks.

---

## Troubleshooting

### Python Not Found
- Ensure Python is installed and accessible.
- Update the PATH environment variable in Jenkins if necessary.

### Permission Denied Errors
- Check the permissions of the Jenkins user.
- Run commands manually as the Jenkins user to debug.

### Dependency Issues
- Validate the `requirements.txt` file.
- Use a consistent Python version across development and Jenkins environments.

---

You are now ready to run Python-based workflows in Jenkins! For more advanced configurations, refer to the [Jenkins documentation](https://www.jenkins.io/doc/).

