package ru.sovcombank.rbs;

//@RequiredArgsConstructor
//@Service
//@Slf4j

public class CheckTestService {
    /*
    private final ValidatorFactory validatorFactory;
    private final TestDataRepository testDataRepository;

    @Autowired
    private TestCaseRunnerFactory testCaseRunnerFactory;

    private TestCaseRunner<?> runner = testCaseRunnerFactory.getTestCaseRunner("MOKE");

    public void checkAllTest(TestProfile profile) {
        TestCaseRunner<?> runner1 =  testCaseRunnerFactory.getTestCaseRunner("MOKE");
        log.info(runner1.getSystem());

        List<TestCase> cases = testDataRepository.loadCases(profile);
        log.debug(cases.toString());
        cases.forEach(testCase -> checkCase(testCase));
    }

    public void checkCase(TestCase testCase) {
        List<ExpectationData> expectations = testCase.getExpectations();

        if (null == expectations || expectations.isEmpty()) {
            log.error("Для тест-кейса {} не заданы ожидания", testCase.getTestCaseData().getDescription());
            return;
        }
        TestCaseData testCaseData = testCase.getTestCaseData();
        log.debug(testCaseData.toString());

        TestResultData resultData = new TestResultData();
        for (ExpectationData expectationData : expectations) {
            log.debug(expectationData.toString());
            Validator validator = validatorFactory.getValidator(expectationData.getValidationType());
            log.debug(validator.toString());

            // Выполняем тест
            TestCaseResponse<?> response = runner.run(testCaseData);
            resultData.setErrorCode(response.getStatus());
            resultData.setValue(response.getValue());
            resultData.setErrorMessage(response.getMessage());
            // Сверяем результат с ожиданием.
            validator.validate(expectationData, resultData);
        }
    }

    public List<String> getRunnerNames() {
        return testCaseRunnerFactory.getAllSystems();
    }

     */
}
