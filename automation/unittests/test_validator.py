import pytest
from automation.core.components.api.validator import Validator


@pytest.fixture
def validator():
    return Validator()


class TestValidator:

    def test_get_uniform_comparator(self, validator):
        assert validator.get_uniform_comparator("eq") == "equals"
        assert validator.get_uniform_comparator("==") == "equals"
        assert validator.get_uniform_comparator("lt") == "less_than"
        assert validator.get_uniform_comparator("le") == "less_than_or_equals"
        assert validator.get_uniform_comparator("gt") == "greater_than"
        assert validator.get_uniform_comparator("ge") == "greater_than_or_equals"
        assert validator.get_uniform_comparator("ne") == "not_equals"

        assert validator.get_uniform_comparator("str_eq") == "string_equals"
        assert validator.get_uniform_comparator("len_eq") == "length_equals"
        assert validator.get_uniform_comparator("count_eq") == "length_equals"

        assert validator.get_uniform_comparator("len_gt") == "length_greater_than"
        assert validator.get_uniform_comparator("count_gt") == "length_greater_than"
        assert validator.get_uniform_comparator("count_greater_than") == "length_greater_than"

        assert validator.get_uniform_comparator("len_ge") == "length_greater_than_or_equals"
        assert validator.get_uniform_comparator("count_ge") == "length_greater_than_or_equals"
        assert validator.get_uniform_comparator("count_greater_than_or_equals") == "length_greater_than_or_equals"

        assert validator.get_uniform_comparator("len_lt") == "length_less_than"
        assert validator.get_uniform_comparator("count_lt") == "length_less_than"
        assert validator.get_uniform_comparator("count_less_than") == "length_less_than"

        assert validator.get_uniform_comparator("len_le") == "length_less_than_or_equals"
        assert validator.get_uniform_comparator("count_le") == "length_less_than_or_equals"
        assert validator.get_uniform_comparator("count_less_than_or_equals") == "length_less_than_or_equals"

    def test_extend_validators_with_dict(self, validator):
        def_validators = [
            {'eq': ["a", {"v": 1}]},
            {'eq': [{"b": 1}, 200]}
        ]
        current_validators = [
            {'len_eq': ['s3', 12]},
            {'eq': [{"b": 1}, 201]}
        ]
        def_validators = [
            validator.uniform_validator(_validator)
            for _validator in def_validators
        ]
        ref_validators = [
            validator.uniform_validator(_validator)
            for _validator in current_validators
        ]

        extended_validators = validator.extend_validators(def_validators, ref_validators)
        assert len(extended_validators) == 3
        assert {'check': {'b': 1}, 'expect': 201, 'comparator': 'equals'} in extended_validators
        assert {'check': {'b': 1}, 'expect': 200, 'comparator': 'equals'}.items() not in extended_validators

    def test_validator_with_part_check(self, validator):
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
        assert validator.validate(expect, check) is False
        check['status_code'] = 200
        assert validator.validate(expect, check) is True

    def test_validator_with_full_check(self, validator):
        expect = {
            'type': 'full_check',
            "points": {
                'status_code': 200,
                "body": {
                    "status": "success",
                    "msg": "hello world"
                }
            }

        }
        check = {
            'status_code': 2000,
            "body": {
                "status": "success",
                "msg": "hello world"
            }
        }
        assert validator.validate(expect, check) is False
        check['status_code'] = 200
        assert validator.validate(expect, check) is True
        check['body']['more'] = 'yes'
        assert validator.validate(expect, check) is False


    def test_validator_with_part_contain_check(self, validator):
        expect = {
            'type': 'part_contain_check',
            "points": {
                'status_code': 200,
                "body": {
                    "status": "success"
                }
            }

        }
        check = {
            'status_code': 2000,
            "body": {
                "status": "success",
                "msg": "hello world"
            }
        }
        assert validator.validate(expect, check) is False
        check['status_code'] = 200
        assert validator.validate(expect, check) is True
        check['body']['more'] = 'yes'
        assert validator.validate(expect, check) is True
        expect['points']['body']['less'] = 'no'
        assert validator.validate(expect, check) is False
