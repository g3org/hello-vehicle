Feature: Hello, Vehicle API

  Background:
    Given the server is running
    Given the scenario considers following data points:
      | timestamp     | vehicleId | sessionId | latitude | longitude | heading |
      | 1519991001975 | veh1      | sess1     | 48.1183  | 11.5398   | 90      |
      | 1519991006975 | veh1      | sess1     | 48.1183  | 11.5404   | 90      |
      | 1519991101975 | veh1      | sess2     | 49.1183  | 12.5398   | 90      |
      | 1519991106975 | veh1      | sess2     | 49.1183  | 12.5404   | 90      |
      | 1520106405654 | veh2      | sess3     | 48.1490  | 11.5508   | 106     |
      | 1520106409654 | veh2      | sess3     | 48.1488  | 11.5515   | 107     |
      | 1520106405653 | veh2      | sess3     | 48.1490  | 11.5508   | 106     |
      | 1520106409651 | veh2      | sess3     | 48.1488  | 11.5515   | 107     |

  Scenario Outline: authentication: <comment>
    When retrieving server status with auth header '<authHeader>'
    Then the API responds with statusCode <statusCode>
    Examples:
      | authHeader | statusCode | comment                    |
      | none       | 401        | empty header => failing    |
      | diverging  | 401        | diverging token => failing |
      | correct    | 200        | successful                 |

  Scenario: store data
    When data is imported via API
    Then the server holds a list of all data points

  Scenario: get all sessions of a vehicle in correct ordering
    Given the data is imported
    When all sessions for vehicle 'veh1' are retrieved
    Then the API returns sessions 'sess1,sess2'

  Scenario: get a single session as an ordered list of the received positions by timestamp
    Given the data is imported
    When all data points for session 'sess3' are retrieved
    Then the API returns the following _ordered_ list of data points:
      | timestamp     | vehicleId | sessionId | latitude | longitude | heading |
      | 1520106405653 | veh2      | sess3     | 48.1490  | 11.5508   | 106     |
      | 1520106405654 | veh2      | sess3     | 48.1490  | 11.5508   | 106     |
      | 1520106409651 | veh2      | sess3     | 48.1488  | 11.5515   | 107     |
      | 1520106409654 | veh2      | sess3     | 48.1488  | 11.5515   | 107     |

  Scenario: get the last position of a certain vehicle
    Given the data is imported
    When the last position of vehicle 'veh2' is retrieved
    Then the API returns the data point with timestamp '1520106409654'