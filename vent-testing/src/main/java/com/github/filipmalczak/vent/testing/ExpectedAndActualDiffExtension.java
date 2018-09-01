package com.github.filipmalczak.vent.testing;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ExpectedAndActualDiffExtension implements AfterTestExecutionCallback {
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Logger logger = LoggerFactory.getLogger(context.getDisplayName());
        Optional<Throwable> thrown = context.getExecutionException();
        if (thrown.isPresent() && thrown.get() instanceof AssertionFailedError){
            AssertionFailedError error = (AssertionFailedError) thrown.get();
            DiffNode diffNode = ObjectDifferBuilder.buildDefault().compare(error.getActual(), error.getExpected());
            logger.error("");
            logger.error("=============== ASSERTION DIFF ===============");
            logger.error("");
            logger.error("Assertion has failed:");
            logger.error("");
            diffNode.visit((node, visit) -> {
                final Object expected = node.canonicalGet(error.getExpected());
                final Object actual = node.canonicalGet(error.getActual());
                logger.error("Value "+node.getPath());
                logger.error("\tExpected: "+expected+"; Actual: "+actual);
            });
            logger.error("");
            logger.error("=============== /ASSERTION DIFF ==============");
        }
    }
}
