Feature: U41 Sales Report

  Scenario: AC2: I can select a period to be reported on.  This might be a single year, month, week or day.
    Given I had 3 sales in June, 2 sales in July and 1 sale in August
    When I request a sales report for July
    Then The 2 sales from July are shown

  Scenario: AC2: I can select a period to be reported on.  This might be a single year, month, week or day.
    Given I had 2 sales in 2019, 3 sales in 2020 and 1 sale in 2021
    When I request a sales report for 2020
    Then The 3 sales from 2020 are shown

  Scenario: AC3: I can also specify a custom period by selecting when it starts and ends.
    Given I had sales on "11/06/2021", "03/07/2021", and "05/09/2021"
    When I select a report from "11/06/2021" to "11/07/2021"
    Then The 2 sales from that period are shown

  Scenario: AC4: I can select the granularity of the report.
  By default, I will just see the total number and total value of all purchases made during the period,
  together with the details of the period, and any other relevant detail (e.g. the business name)..
    Given In June I had one sale worth $3.00 and one sale worth $12.00
    When I request a sales report for June
    Then The report shows a total number of 2

  Scenario: AC4: I can select the granularity of the report.
  By default, I will just see the total number and total value of all purchases made during the period,
  together with the details of the period, and any other relevant detail (e.g. the business name)..
    Given In June I had one sale worth $3.00 and one sale worth $12.00
    When I request a sales report for June
    Then The report shows a total value of $15.00

  Scenario: AC5: I can also select finer granularity (e.g. monthly).
  In this case the report would have a line for each month, including the month name/number
  and the correspondent total number and total value for that month.
    Given I had 3 sales in June, 2 sales in July and 1 sale in August
    When I request a sales report from June to August with monthly granularity
    Then The report shows total numbers of 3 for June, 2 for July and 1 for August

  Scenario: AC5: I can also select finer granularity (e.g. monthly).
  In this case the report would have a line for each month, including the month name/number
  and the correspondent total number and total value for that month.
    Given I had $65.00 worth of sales in June, $48.00 worth of sales in July and $15.00 worth of sales in August
    When I request a sales report from June to August with monthly granularity
    Then The report shows total values of $65.00 for June, $48.00 for July and $15.00 for August
