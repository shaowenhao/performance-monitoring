import io
import types
import json
import collections
from collections.abc import Mapping, Iterable
from deepdiff import DeepDiff
from automation.config.config import Config
from automation.core import exceptions, logger, utils
from automation.core.components.api import comparators


class Validator(object):

    def validate(self, expect, check):
        self._check_validate_format(expect)
        _type = expect['type']
        if _type == 'full_check':
            return self._validate_with_full_check(expect['points'], check)
        if _type == 'part_check':
            return self._validate_with_part_check(expect['points'], check)
        if _type == 'part_contain_check':
            return self._validate_with_part_contain_check(expect['points'], check)
        return True

    def _validate_with_full_check(self, expect, check):
        ddiff = DeepDiff(expect, check, ignore_order=True, report_repetition=True)
        if ddiff:
            logger.log_error("there's different between expect and check, detail is \n {}".format(ddiff))
            return False
        return True

    def _validate_with_part_check(self, expect, check):
        validators = [
            self.uniform_validator(_validator)
            for _validator in expect
        ]

        """ make validation with comparators
        """
        self.validation_results = []
        if not validators:
            return

        logger.log_debug("start to validate.")

        validate_pass = True
        failures = []

        for _validator in validators:
            check_item, expect_value = _validator['check'],  _validator['expect']
            check_value = utils.query_json(check, check_item)

            comparator = _validator['comparator']
            validator_dict = {
                "comparator": comparator,
                "check_item": check_item,
                "check_value": check_value,
                "expect_value": expect_value
            }
            validate_msg = "\nvalidate: {}".format(validator_dict)

            try:
                method = getattr(comparators, comparator)
                method(check_value, expect_value)
                logger.log_debug(validate_msg)
            except (AssertionError, TypeError):
                validate_pass = False
                validator_dict["check_result"] = "fail"
                validate_msg += "\t==> fail"
                validate_msg += "\n{}({}) {} {}({})".format(
                    check_value,
                    type(check_value).__name__,
                    comparator,
                    expect_value,
                    type(expect_value).__name__
                )
                logger.log_error(validate_msg)
                failures.append(validate_msg)

            self.validation_results.append(validator_dict)

        logger.log_info('validate msg: \n {} \n'.format(self.validation_results))
        return validate_pass

    def _validate_with_part_contain_check(self, expect, check):
        # if expect.items() <= check.items():
        if self._check_dict_contain_another(check, expect):
            return True
        logger.log_error("part check failed, expect is \n {} \n check is \n {}".format(expect, check))
        return False

    def _check_dict_contain_another(self, superdict, subdict):
        check_result = True
        not_contain_keys = list(set(subdict.keys()) - set(superdict.keys()))
        if not_contain_keys:
            logger.log_error("Keys {} not in the check dict".format(not_contain_keys))
            check_result = False
            return check_result
        for key, values in subdict.items():
            if isinstance(values, type(superdict[key])):
                if isinstance(values, Mapping):
                    if not self._check_dict_contain_another(superdict[key], values):
                        check_result = False
                        break
                elif isinstance(values, list):
                    ddiff = DeepDiff(values, superdict[key], ignore_order=True)
                    if ddiff:
                        logger.log_error("Key {} with values {} not in the check dict".format(key, ddiff))
                        check_result = False
                        break
                else:
                    if values != superdict[key]:
                        logger.log_error("Key {} with values {} not same with check dict {}".format(key, values, superdict[key]))
                        check_result = False
                        break
        return check_result




    def get_uniform_comparator(self, comparator):
        """ convert comparator alias to uniform name
        """
        if comparator in ["eq", "equals", "==", "is"]:
            return "equals"
        elif comparator in ["lt", "less_than"]:
            return "less_than"
        elif comparator in ["le", "less_than_or_equals"]:
            return "less_than_or_equals"
        elif comparator in ["gt", "greater_than"]:
            return "greater_than"
        elif comparator in ["ge", "greater_than_or_equals"]:
            return "greater_than_or_equals"
        elif comparator in ["ne", "not_equals"]:
            return "not_equals"
        elif comparator in ["str_eq", "string_equals"]:
            return "string_equals"
        elif comparator in ["len_eq", "length_equals", "count_eq"]:
            return "length_equals"
        elif comparator in ["len_gt", "count_gt", "length_greater_than", "count_greater_than"]:
            return "length_greater_than"
        elif comparator in ["len_ge", "count_ge", "length_greater_than_or_equals",
                            "count_greater_than_or_equals"]:
            return "length_greater_than_or_equals"
        elif comparator in ["len_lt", "count_lt", "length_less_than", "count_less_than"]:
            return "length_less_than"
        elif comparator in ["len_le", "count_le", "length_less_than_or_equals",
                            "count_less_than_or_equals"]:
            return "length_less_than_or_equals"
        elif comparator in ["re", "regex", "regex_match"]:
            return "regex_match"
        else:
            return comparator

    def uniform_validator(self, validator):
        """ unify validator

        Args:
            validator (dict): validator maybe in two formats:

                format1: this is kept for compatiblity with the previous versions.
                    {"check": "status_code", "comparator": "eq", "expect": 201}
                    {"check": "$resp_body_success", "comparator": "eq", "expect": True}
                format2: recommended new version, {comparator: [check_item, expected_value]}
                    {'eq': ['status_code', 201]}
                    {'eq': ['$resp_body_success', True]}

        Returns
            dict: validator info

                {
                    "check": "status_code",
                    "expect": 201,
                    "comparator": "equals"
                }

        """
        if not isinstance(validator, dict):
            raise exceptions.ParamsError("invalid validator: {}".format(validator))

        if "check" in validator and "expect" in validator:
            # format1
            check_item = validator["check"]
            expect_value = validator["expect"]
            comparator = validator.get("comparator", "eq")

        elif len(validator) == 1:
            # format2
            comparator = list(validator.keys())[0]
            compare_values = validator[comparator]

            if not isinstance(compare_values, list) or len(compare_values) != 2:
                raise exceptions.ParamsError("invalid validator: {}".format(validator))

            check_item, expect_value = compare_values

        else:
            raise exceptions.ParamsError("invalid validator: {}".format(validator))

        # uniform comparator, e.g. lt => less_than, eq => equals
        comparator = self.get_uniform_comparator(comparator)

        return {
            "check": check_item,
            "expect": expect_value,
            "comparator": comparator
        }

    def _convert_validators_to_mapping(self, validators):
        """ convert validators list to mapping.

        Args:
            validators (list): validators in list

        Returns:
            dict: validators mapping, use (check, comparator) as key.

        Examples:
            >>> validators = [
                    {"check": "v1", "expect": 201, "comparator": "eq"},
                    {"check": {"b": 1}, "expect": 200, "comparator": "eq"}
                ]
            >>> _convert_validators_to_mapping(validators)
                {
                    ("v1", "eq"): {"check": "v1", "expect": 201, "comparator": "eq"},
                    ('{"b": 1}', "eq"): {"check": {"b": 1}, "expect": 200, "comparator": "eq"}
                }

        """
        validators_mapping = {}

        for validator in validators:
            if not isinstance(validator["check"], collections.Hashable):
                check = json.dumps(validator["check"])
            else:
                check = validator["check"]

            key = (check, validator["comparator"])
            validators_mapping[key] = validator

        return validators_mapping

    def extend_validators(self, raw_validators, override_validators):
        """ extend raw_validators with override_validators.
            override_validators will merge and override raw_validators.

        Args:
            raw_validators (dict):
            override_validators (dict):

        Returns:
            list: extended validators

        Examples:
            >>> raw_validators = [{'eq': ['v1', 200]}, {"check": "s2", "expect": 16, "comparator": "len_eq"}]
            >>> override_validators = [{"check": "v1", "expect": 201}, {'len_eq': ['s3', 12]}]
            >>> extend_validators(raw_validators, override_validators)
                [
                    {"check": "v1", "expect": 201, "comparator": "eq"},
                    {"check": "s2", "expect": 16, "comparator": "len_eq"},
                    {"check": "s3", "expect": 12, "comparator": "len_eq"}
                ]

        """

        if not raw_validators:
            return override_validators

        elif not override_validators:
            return raw_validators

        else:
            def_validators_mapping = self._convert_validators_to_mapping(raw_validators)
            ref_validators_mapping = self._convert_validators_to_mapping(override_validators)

            def_validators_mapping.update(ref_validators_mapping)
            return list(def_validators_mapping.values())

    ###############################################################################
    ##   validate varibles and functions
    ###############################################################################

    def is_function(self, item):
        """ Takes item object, returns True if it is a function.
        """
        return isinstance(item, types.FunctionType)

    def is_variable(self, tup):
        """ Takes (name, object) tuple, returns True if it is a variable.
        """
        name, item = tup
        if callable(item):
            # function or class
            return False

        if isinstance(item, types.ModuleType):
            # imported module
            return False

        if name.startswith("_"):
            # private property
            return False

        return True

    def _check_validate_format(self, expect):
        """ Check validate format
        """
        if not isinstance(expect, dict):
            err_msg = "invalid validate format, dict suppose: {}".format(expect)
            logger.log_error(err_msg)
            raise exceptions.ParamsError(err_msg)
        if "type" in expect and "points" in expect:
            if expect['type'] not in Config.VALIDATE_TYPES:
                err_msg = "invalid validator type: {} , should be in {}".format(expect, Config.VALIDATE_TYPES)
                logger.log_error(err_msg)
                raise exceptions.ParamsError(err_msg)
        else:
            err_msg = "invalid validator format: {} , there should be type and points in the key".format(expect)
            logger.log_error(err_msg)
            raise exceptions.ParamsError(err_msg)

    def validate_json_file(self, file_list):
        """ validate JSON testcase format
        """
        for json_file in set(file_list):
            if not json_file.endswith(".json"):
                logger.log_warning("Only JSON file format can be validated, skip: {}".format(json_file))
                continue

            logger.color_print("Start to validate JSON file: {}".format(json_file), "GREEN")

            with io.open(json_file) as stream:
                try:
                    json.load(stream)
                except ValueError as e:
                    raise SystemExit(e)

            print("OK")


if __name__ == '__main__':
    expect = {
        'type': 'part_check',
        "points": [
            {
                "eq": [
                    "status_code",
                    200
                ]
            },
            {
                "eq": [
                    "body.status",
                    "success"
                ]
            }
        ]

    }
    check = {
        'status_code': 2000,
        "body": {
            "status": "success",
            "msg": "hello world"
        }
    }
    v = Validator()
    v.validate(expect, check)
