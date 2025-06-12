package com.tutoringplatform;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Tutoring Platform Test Suite")
@SelectPackages({
        "com.tutoringplatform.command",
        "com.tutoringplatform.controllers",
        "com.tutoringplatform.models",
        "com.tutoringplatform.observer",
        "com.tutoringplatform.repositories.impl",
        "com.tutoringplatform.services"
})
public class TestSuiteRunner {
}
