package pl.krasnoludkolo.ebet2.external.clients.footballdata;

import org.json.JSONArray;
import org.json.JSONObject;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

final class StubFootballDataDownloader extends FootballDataDownloader {

    @Override
    JSONArray downloadAllRounds(ExternalSourceConfiguration config) {
        return new JSONObject(json).getJSONArray("matches");
    }

    private String json = "{\n" +
            "  \"count\": 552,\n" +
            "  \"filters\": {},\n" +
            "  \"competition\": {\n" +
            "    \"id\": 2016,\n" +
            "    \"area\": {\n" +
            "      \"id\": 2072,\n" +
            "      \"name\": \"England\"\n" +
            "    },\n" +
            "    \"name\": \"Championship\",\n" +
            "    \"code\": \"ELC\",\n" +
            "    \"plan\": \"TIER_ONE\",\n" +
            "    \"lastUpdated\": \"2019-04-10T23:44:25Z\"\n" +
            "  },\n" +
            "  \"matches\": [\n" +
            "    {\n" +
            "      \"id\": 234631,\n" +
            "      \"season\": {\n" +
            "        \"id\": 154,\n" +
            "        \"startDate\": \"2018-08-03\",\n" +
            "        \"endDate\": \"2019-05-05\",\n" +
            "        \"currentMatchday\": 41\n" +
            "      },\n" +
            "      \"utcDate\": \"2018-08-03T19:00:00Z\",\n" +
            "      \"status\": \"FINISHED\",\n" +
            "      \"matchday\": 1,\n" +
            "      \"stage\": \"REGULAR_SEASON\",\n" +
            "      \"group\": \"Regular Season\",\n" +
            "      \"lastUpdated\": \"2019-01-19T19:32:54Z\",\n" +
            "      \"score\": {\n" +
            "        \"winner\": \"AWAY_TEAM\",\n" +
            "        \"duration\": \"REGULAR\",\n" +
            "        \"fullTime\": {\n" +
            "          \"homeTeam\": 1,\n" +
            "          \"awayTeam\": 2\n" +
            "        },\n" +
            "        \"halfTime\": {\n" +
            "          \"homeTeam\": 0,\n" +
            "          \"awayTeam\": 0\n" +
            "        },\n" +
            "        \"extraTime\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        },\n" +
            "        \"penalties\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        }\n" +
            "      },\n" +
            "      \"homeTeam\": {\n" +
            "        \"id\": 355,\n" +
            "        \"name\": \"Reading FC\"\n" +
            "      },\n" +
            "      \"awayTeam\": {\n" +
            "        \"id\": 342,\n" +
            "        \"name\": \"Derby County FC\"\n" +
            "      },\n" +
            "      \"referees\": [\n" +
            "        {\n" +
            "          \"id\": 11446,\n" +
            "          \"name\": \"Robert Jones\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11325,\n" +
            "          \"name\": \"Nigel Lugg\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11485,\n" +
            "          \"name\": \"Michael George\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11290,\n" +
            "          \"name\": \"Steve Martin\",\n" +
            "          \"nationality\": null\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 234632,\n" +
            "      \"season\": {\n" +
            "        \"id\": 154,\n" +
            "        \"startDate\": \"2018-08-03\",\n" +
            "        \"endDate\": \"2019-05-05\",\n" +
            "        \"currentMatchday\": 41\n" +
            "      },\n" +
            "      \"utcDate\": \"2018-08-04T14:00:00Z\",\n" +
            "      \"status\": \"FINISHED\",\n" +
            "      \"matchday\": 1,\n" +
            "      \"stage\": \"REGULAR_SEASON\",\n" +
            "      \"group\": \"Regular Season\",\n" +
            "      \"lastUpdated\": \"2019-01-19T19:32:54Z\",\n" +
            "      \"score\": {\n" +
            "        \"winner\": \"AWAY_TEAM\",\n" +
            "        \"duration\": \"REGULAR\",\n" +
            "        \"fullTime\": {\n" +
            "          \"homeTeam\": 1,\n" +
            "          \"awayTeam\": 2\n" +
            "        },\n" +
            "        \"halfTime\": {\n" +
            "          \"homeTeam\": 1,\n" +
            "          \"awayTeam\": 1\n" +
            "        },\n" +
            "        \"extraTime\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        },\n" +
            "        \"penalties\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        }\n" +
            "      },\n" +
            "      \"homeTeam\": {\n" +
            "        \"id\": 74,\n" +
            "        \"name\": \"West Bromwich Albion FC\"\n" +
            "      },\n" +
            "      \"awayTeam\": {\n" +
            "        \"id\": 60,\n" +
            "        \"name\": \"Bolton Wanderers FC\"\n" +
            "      },\n" +
            "      \"referees\": [\n" +
            "        {\n" +
            "          \"id\": 11312,\n" +
            "          \"name\": \"Geoff Eltringham\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11311,\n" +
            "          \"name\": \"Paul Hodskinson\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11380,\n" +
            "          \"name\": \"Billy Smallwood\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11332,\n" +
            "          \"name\": \"Scott Duncan\",\n" +
            "          \"nationality\": null\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 234633,\n" +
            "      \"season\": {\n" +
            "        \"id\": 154,\n" +
            "        \"startDate\": \"2018-08-03\",\n" +
            "        \"endDate\": \"2019-05-05\",\n" +
            "        \"currentMatchday\": 41\n" +
            "      },\n" +
            "      \"utcDate\": \"2018-08-04T14:00:00Z\",\n" +
            "      \"status\": \"FINISHED\",\n" +
            "      \"matchday\": 1,\n" +
            "      \"stage\": \"REGULAR_SEASON\",\n" +
            "      \"group\": \"Regular Season\",\n" +
            "      \"lastUpdated\": \"2019-01-19T19:32:54Z\",\n" +
            "      \"score\": {\n" +
            "        \"winner\": \"HOME_TEAM\",\n" +
            "        \"duration\": \"REGULAR\",\n" +
            "        \"fullTime\": {\n" +
            "          \"homeTeam\": 3,\n" +
            "          \"awayTeam\": 2\n" +
            "        },\n" +
            "        \"halfTime\": {\n" +
            "          \"homeTeam\": 2,\n" +
            "          \"awayTeam\": 1\n" +
            "        },\n" +
            "        \"extraTime\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        },\n" +
            "        \"penalties\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        }\n" +
            "      },\n" +
            "      \"homeTeam\": {\n" +
            "        \"id\": 75,\n" +
            "        \"name\": \"Wigan Athletic FC\"\n" +
            "      },\n" +
            "      \"awayTeam\": {\n" +
            "        \"id\": 345,\n" +
            "        \"name\": \"Sheffield Wednesday FC\"\n" +
            "      },\n" +
            "      \"referees\": [\n" +
            "        {\n" +
            "          \"id\": 11396,\n" +
            "          \"name\": \"Tim Robinson\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11318,\n" +
            "          \"name\": \"Mark Jones\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11397,\n" +
            "          \"name\": \"Matt McGrath\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11626,\n" +
            "          \"name\": \"Matt Donohue\",\n" +
            "          \"nationality\": null\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 234634,\n" +
            "      \"season\": {\n" +
            "        \"id\": 154,\n" +
            "        \"startDate\": \"2018-08-03\",\n" +
            "        \"endDate\": \"2019-05-05\",\n" +
            "        \"currentMatchday\": 41\n" +
            "      },\n" +
            "      \"utcDate\": \"2018-08-04T14:00:00Z\",\n" +
            "      \"status\": \"FINISHED\",\n" +
            "      \"matchday\": 1,\n" +
            "      \"stage\": \"REGULAR_SEASON\",\n" +
            "      \"group\": \"Regular Season\",\n" +
            "      \"lastUpdated\": \"2019-01-19T19:32:54Z\",\n" +
            "      \"score\": {\n" +
            "        \"winner\": \"DRAW\",\n" +
            "        \"duration\": \"REGULAR\",\n" +
            "        \"fullTime\": {\n" +
            "          \"homeTeam\": 2,\n" +
            "          \"awayTeam\": 2\n" +
            "        },\n" +
            "        \"halfTime\": {\n" +
            "          \"homeTeam\": 0,\n" +
            "          \"awayTeam\": 0\n" +
            "        },\n" +
            "        \"extraTime\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        },\n" +
            "        \"penalties\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        }\n" +
            "      },\n" +
            "      \"homeTeam\": {\n" +
            "        \"id\": 332,\n" +
            "        \"name\": \"Birmingham City FC\"\n" +
            "      },\n" +
            "      \"awayTeam\": {\n" +
            "        \"id\": 68,\n" +
            "        \"name\": \"Norwich City FC\"\n" +
            "      },\n" +
            "      \"referees\": [\n" +
            "        {\n" +
            "          \"id\": 11309,\n" +
            "          \"name\": \"Peter Bankes\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11460,\n" +
            "          \"name\": \"Geoff Russell\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11457,\n" +
            "          \"name\": \"Lee Venamore\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11290,\n" +
            "          \"name\": \"Steve Martin\",\n" +
            "          \"nationality\": null\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 234635,\n" +
            "      \"season\": {\n" +
            "        \"id\": 154,\n" +
            "        \"startDate\": \"2018-08-03\",\n" +
            "        \"endDate\": \"2019-05-05\",\n" +
            "        \"currentMatchday\": 41\n" +
            "      },\n" +
            "      \"utcDate\": \"2018-08-04T14:00:00Z\",\n" +
            "      \"status\": \"FINISHED\",\n" +
            "      \"matchday\": 1,\n" +
            "      \"stage\": \"REGULAR_SEASON\",\n" +
            "      \"group\": \"Regular Season\",\n" +
            "      \"lastUpdated\": \"2019-01-19T19:32:54Z\",\n" +
            "      \"score\": {\n" +
            "        \"winner\": \"HOME_TEAM\",\n" +
            "        \"duration\": \"REGULAR\",\n" +
            "        \"fullTime\": {\n" +
            "          \"homeTeam\": 1,\n" +
            "          \"awayTeam\": 0\n" +
            "        },\n" +
            "        \"halfTime\": {\n" +
            "          \"homeTeam\": 0,\n" +
            "          \"awayTeam\": 0\n" +
            "        },\n" +
            "        \"extraTime\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        },\n" +
            "        \"penalties\": {\n" +
            "          \"homeTeam\": null,\n" +
            "          \"awayTeam\": null\n" +
            "        }\n" +
            "      },\n" +
            "      \"homeTeam\": {\n" +
            "        \"id\": 1081,\n" +
            "        \"name\": \"Preston North End FC\"\n" +
            "      },\n" +
            "      \"awayTeam\": {\n" +
            "        \"id\": 69,\n" +
            "        \"name\": \"Queens Park Rangers FC\"\n" +
            "      },\n" +
            "      \"referees\": [\n" +
            "        {\n" +
            "          \"id\": 11469,\n" +
            "          \"name\": \"Darren England\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11403,\n" +
            "          \"name\": \"Nick Greenhalgh\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11471,\n" +
            "          \"name\": \"Mark Dwyer\",\n" +
            "          \"nationality\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": 11561,\n" +
            "          \"name\": \"Tom Nield\",\n" +
            "          \"nationality\": null\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
