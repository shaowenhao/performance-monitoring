from automation.core.components.api.test_step import TestStep
from automation.core import logger


def call_api(data, http_client=None):
    try:
        test_step = TestStep(data, http_client=http_client)
        test_step.before_test()
        response = test_step.test()
        test_step.after_test()
        return response
    except AssertionError:
        raise
    except Exception as e:
        logger.log_error("Exception raised, mark test failed, detail is \n {} \n".format(e))
        raise AssertionError(e)
