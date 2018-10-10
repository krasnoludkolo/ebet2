package pl.krasnoludkolo.ebet2.external.clients.footballdata;

import io.vavr.collection.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class FootballDataClientTest {


    @Mock
    private FootballDataDownloader downloader;

    private FootballDataClient client;
    private ExternalSourceConfiguration config;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        client = FootballDataClient.create(downloader);
        config = new ExternalSourceConfiguration();
        JSONArray fixedList = loadFromFile();
        when(downloader.downloadAllRounds(config)).thenReturn(fixedList);
    }

    private JSONArray loadFromFile() {
        return new JSONObject(json).getJSONArray("fixtures");
    }

    @Test
    public void shouldMapDownloadedData() {
        //when
        List<MatchInfo> matchInfos = client.downloadAllRounds(config);
        //then
        assertEquals(5, matchInfos.size());
        for (MatchInfo info : matchInfos) {
            assertNotNull(info);
        }
    }

    private String json = "{\n" +
            "  \"_links\": {\n" +
            "    \"self\": {\n" +
            "      \"href\": \"http://api.football-data.org/v1/competitions/450/fixtures\"\n" +
            "    },\n" +
            "    \"competition\": {\n" +
            "      \"href\": \"http://api.football-data.org/v1/competitions/450\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"count\": 6,\n" +
            "  \"fixtures\": [\n" +
            "    {\n" +
            "      \"_links\": {\n" +
            "        \"self\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/fixtures/161661\"\n" +
            "        },\n" +
            "        \"competition\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/competitions/450\"\n" +
            "        },\n" +
            "        \"homeTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/548\"\n" +
            "        },\n" +
            "        \"awayTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/511\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"date\": \"2017-08-04T18:45:00Z\",\n" +
            "      \"status\": \"FINISHED\",\n" +
            "      \"matchday\": 1,\n" +
            "      \"homeTeamName\": \"AS Monaco FC\",\n" +
            "      \"awayTeamName\": \"Toulouse FC\",\n" +
            "      \"result\": {\n" +
            "        \"goalsHomeTeam\": 3,\n" +
            "        \"goalsAwayTeam\": 2,\n" +
            "        \"halfTime\": {\n" +
            "          \"goalsHomeTeam\": 1,\n" +
            "          \"goalsAwayTeam\": 1\n" +
            "        }\n" +
            "      },\n" +
            "      \"odds\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"_links\": {\n" +
            "        \"self\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/fixtures/161665\"\n" +
            "        },\n" +
            "        \"competition\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/competitions/450\"\n" +
            "        },\n" +
            "        \"homeTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/524\"\n" +
            "        },\n" +
            "        \"awayTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/530\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"date\": \"2017-08-05T15:15:00Z\",\n" +
            "      \"status\": \"FINISHED\",\n" +
            "      \"matchday\": 1,\n" +
            "      \"homeTeamName\": \"Paris Saint-Germain\",\n" +
            "      \"awayTeamName\": \"Amiens SC\",\n" +
            "      \"result\": {\n" +
            "        \"goalsHomeTeam\": 2,\n" +
            "        \"goalsAwayTeam\": 0,\n" +
            "        \"halfTime\": {\n" +
            "          \"goalsHomeTeam\": 1,\n" +
            "          \"goalsAwayTeam\": 0\n" +
            "        }\n" +
            "      },\n" +
            "      \"odds\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"_links\": {\n" +
            "        \"self\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/fixtures/161662\"\n" +
            "        },\n" +
            "        \"competition\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/competitions/450\"\n" +
            "        },\n" +
            "        \"homeTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/518\"\n" +
            "        },\n" +
            "        \"awayTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/514\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"date\": \"2017-08-05T18:00:00Z\",\n" +
            "      \"status\": \"FINISHED\",\n" +
            "      \"matchday\": 1,\n" +
            "      \"homeTeamName\": \"Montpellier Hérault SC\",\n" +
            "      \"awayTeamName\": \"SM Caen\",\n" +
            "      \"result\": {\n" +
            "        \"goalsHomeTeam\": 1,\n" +
            "        \"goalsAwayTeam\": 0,\n" +
            "        \"halfTime\": {\n" +
            "          \"goalsHomeTeam\": 0,\n" +
            "          \"goalsAwayTeam\": 0\n" +
            "        }\n" +
            "      },\n" +
            "      \"odds\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"_links\": {\n" +
            "        \"self\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/fixtures/161667\"\n" +
            "        },\n" +
            "        \"competition\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/competitions/450\"\n" +
            "        },\n" +
            "        \"homeTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/531\"\n" +
            "        },\n" +
            "        \"awayTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/529\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"date\": \"2017-08-05T18:00:00Z\",\n" +
            "      \"status\": \"FINISHED\",\n" +
            "      \"matchday\": 1,\n" +
            "      \"homeTeamName\": \"ES Troyes AC\",\n" +
            "      \"awayTeamName\": \"Stade Rennais FC\",\n" +
            "      \"result\": {\n" +
            "        \"goalsHomeTeam\": 1,\n" +
            "        \"goalsAwayTeam\": 1,\n" +
            "        \"halfTime\": {\n" +
            "          \"goalsHomeTeam\": 0,\n" +
            "          \"goalsAwayTeam\": 0\n" +
            "        }\n" +
            "      },\n" +
            "      \"odds\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"_links\": {\n" +
            "        \"self\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/fixtures/161660\"\n" +
            "        },\n" +
            "        \"competition\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/competitions/450\"\n" +
            "        },\n" +
            "        \"homeTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/545\"\n" +
            "        },\n" +
            "        \"awayTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/538\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"date\": \"3017-08-05T18:00:00Z\",\n" +
            "      \"status\": \"SCHEDULED\",\n" +
            "      \"matchday\": 11,\n" +
            "      \"homeTeamName\": \"\",\n" +
            "      \"awayTeamName\": \"\",\n" +
            "      \"result\": {\n" +
            "        \"goalsHomeTeam\": null,\n" +
            "        \"goalsAwayTeam\": null\n" +
            "      },\n" +
            "      \"odds\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"_links\": {\n" +
            "        \"self\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/fixtures/161663\"\n" +
            "        },\n" +
            "        \"competition\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/competitions/450\"\n" +
            "        },\n" +
            "        \"homeTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/523\"\n" +
            "        },\n" +
            "        \"awayTeam\": {\n" +
            "          \"href\": \"http://api.football-data.org/v1/teams/576\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"date\": \"3017-08-05T18:00:00Z\",\n" +
            "      \"status\": \"TIMED\",\n" +
            "      \"matchday\": 11,\n" +
            "      \"homeTeamName\": \"Olympique Lyonnais\",\n" +
            "      \"awayTeamName\": \"RC Strasbourg Alsace\",\n" +
            "      \"result\": {\n" +
            "        \"goalsHomeTeam\": null,\n" +
            "        \"goalsAwayTeam\": null\n" +
            "      },\n" +
            "      \"odds\": null\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
