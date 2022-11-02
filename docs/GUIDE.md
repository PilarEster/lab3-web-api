# GUIDE

This is the repository that we will use for the second assignment of the course 2022-2023. This guide is command line oriented, but you are free to use IDE like _VS Code_, _IntelliJ IDEA_ and _Eclipse_ which have full support of the tools that we are going to use. We also assume that you have installed in your box at least [Kotlin 1.7.0](https://kotlinlang.org/docs/getting-started.html#install-kotlin).

This laboratory is not a speed competition.

## Preparation

Fork this repository.
Next you will have <https://github.com/UNIZAR-30246-WebEngineering/lab3-web-api> cloned in `https://github.com/your-github-username/lab3-web-api`.

By default, GitHub Actions is disabled for your forked repository.
Go to `https://github.com/your-github-username/lab3-web-api/actions` and enable them.

Next, go to your repository and click in `Code` on the `main` button and create a branch named `work`.

Next, clone locally the repository:

```bash
git clone https://github.com/your-github-username/lab3-web-api
cd lab3-web-api
git branch -a
```

Should show `main`, `work`, `remotes/origin/main` and `remotes/origin/work`.

Then, checkout the `work` branch:

```bash
git checkout -b work
```

Make changes to the files, commit the changes to the history and push the branch up to your forked version.

```bash
git push origin work
```

If you want to run the tests, just run:

```bash
./gradlew test
```

## Primary task

- Complete tests of `ControllerTests`

## Steps required

There are 4 test (`POST`, `GET`, `PUT`, `DELETE`).

The code blocks below must be used to complete the setup of the test or to complete the verification part of the test.
Each test must be completed with one or more **setup blocks** and one or more **validation blocks** to verify the behaviour tested.

**Setup block**: ensure that the repository returns a specific employee with `id = 1`.

```kotlin
every {
    employeeRepository.findById(1)
} answers {
    Optional.of(Employee("Mary", "Manager", 1))
}
```

**Setup block**: ensure that the repository does not contain an employee with `id = 2`.

```kotlin
every {
    employeeRepository.findById(2)
} answers {
    Optional.empty()
}
```

**Setup block**: ensure in first call that the repository does not contain an employee with `id = 1` and a specific employee in the second call.

```kotlin
every {
    employeeRepository.findById(1)
} answers {
    Optional.empty()
} andThenAnswer {
    Optional.of(Employee("Tom", "Manager", 1))
}
```

**Setup block**: ensure in first call that the repository contain a specific with `id = 1` and none identified by `id = 1` in the second call.

```kotlin
every {
    employeeRepository.findById(1)
} answers {
    Optional.of(Employee("Tom", "Manager", 1))
} andThenAnswer {
    Optional.empty()
}
```

**Setup block**: ensure that the call to delete an employee identified by `id = 1` works.

```kotlin
justRun {
    employeeRepository.deleteById(1)
}
```

**Setup block**: ensure in first call that the repository save the employee with `id = 1` and with `id = 2` in the second call.

```kotlin
val employee = slot<Employee>()
every {
    employeeRepository.save(capture(employee))
} answers {
    employee.captured.copy(id = 1)
} andThenAnswer {
    employee.captured.copy(id = 2)
}
```

**Setup block**: ensure that the repository save the employee without modifying its `id`.

```kotlin
val employee = slot<Employee>()
every {
    employeeRepository.save(capture(employee))
} answers {
    employee.captured
}
```

**Verify block**: verify that `save` has been called twice with a new employee without `id`.

```kotlin
verify(exactly = 2) {
    employeeRepository.save(Employee("Mary", "Manager"))
}
```

**Verify block**: verify that `save` has been called twice with an employee with `id = 1`.

```kotlin
verify(exactly = 2) {
    employeeRepository.save(Employee("Tom", "Manager", 1))
}
```

**Verify block**: verify that `findById(1)` has been called twice.

```kotlin
verify(exactly = 2) {
    employeeRepository.findById(1)
}
```

**Verify block**: verify methods that modify the repository have not been invoked.

```kotlin
verify(exactly = 0) {
    employeeRepository.save(any())
    employeeRepository.deleteById(any())
}
```

**Verify block**: verify that `deleteById(1)` has been called once.

```kotlin
verify(exactly = 1) {
    employeeRepository.deleteById(1)
}
```

## Manual verification

1. Ensure that if the method is safe, the methods that modify the state of the repository have not been invoked.

2. Ensure that if the method is idempotent, the methods that modify the state of the repository are either invoked once or, if they are invoked twice, the second call does not modify the state.

3. Pass the tests

   ```sh
   ./gradlew test  
   ```

## How to submit

In your master branch update your corresponding row in README.md with the link to your work branch, the GitHub actions badge for CI and a link to a document that proof or explains how you solved this lab.

Do a pull request from your `main` branch to this repo main branch.
The only file modified in the pull request must be `README.md`
The pull request will be accepted if:

- Your `work` branch contains proofs that shows that you have fulfilled the primary tasks.
- In `README.md` you provides a link to your work branch in your repository, i.e.:

  ```md
  [your-github-user-name](https://github.com/your-github-username/lab3-web-api/tree/work)
  ```

  along with your NIA and a link to your CI workflow.

  ```md
  ![Build Status](https://github.com/your-github-username/lab3-web-api/actions/workflows/CI.yml/badge.svg?branch=work&event=push)](https://github.com/your-github-username/lab3-web-api/actions/workflows/CI.yml)  
  ```

  and the color of your CI workflow triggered by a push event in the branch named work is green.
