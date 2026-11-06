export interface Coordinates {
  state: string;
  capital: string;
  latitude: number;
  longitude: number;
}

export function getCoordinatesByState(
  stateName: string,
): { latitude: number; longitude: number } | null {
  const stateData = stateCoordinates.find(
    (c) => c.state.toLowerCase() === stateName.toLowerCase(),
  );
  if (stateData) {
    return { latitude: stateData.latitude, longitude: stateData.longitude };
  } else {
    console.error("State not found.");
    return null;
  }
}

export const stateCoordinates: Coordinates[] = [
  {
    state: "Alabama",
    capital: "Montgomery",
    latitude: 32.3777,
    longitude: -86.3,
  },
  {
    state: "Alaska",
    capital: "Juneau",
    latitude: 58.3019,
    longitude: -134.4197,
  },
  {
    state: "Arizona",
    capital: "Phoenix",
    latitude: 33.4484,
    longitude: -112.074,
  },
  {
    state: "Arkansas",
    capital: "Little Rock",
    latitude: 34.7465,
    longitude: -92.2896,
  },
  {
    state: "California",
    capital: "Sacramento",
    latitude: 38.5767,
    longitude: -121.4936,
  },
  {
    state: "Colorado",
    capital: "Denver",
    latitude: 39.7392,
    longitude: -104.9903,
  },
  {
    state: "Connecticut",
    capital: "Hartford",
    latitude: 41.764,
    longitude: -72.6822,
  },
  {
    state: "Delaware",
    capital: "Dover",
    latitude: 39.1573,
    longitude: -75.5199,
  },
  {
    state: "Florida",
    capital: "Tallahassee",
    latitude: 30.4383,
    longitude: -84.2807,
  },
  {
    state: "Georgia",
    capital: "Atlanta",
    latitude: 33.749,
    longitude: -84.388,
  },
  {
    state: "Hawaii",
    capital: "Honolulu",
    latitude: 21.3069,
    longitude: -157.8583,
  },
  { state: "Idaho", capital: "Boise", latitude: 43.615, longitude: -116.2023 },
  {
    state: "Illinois",
    capital: "Springfield",
    latitude: 39.7983,
    longitude: -89.6544,
  },
  {
    state: "Indiana",
    capital: "Indianapolis",
    latitude: 39.7684,
    longitude: -86.1581,
  },
  {
    state: "Iowa",
    capital: "Des Moines",
    latitude: 41.5868,
    longitude: -93.625,
  },
  {
    state: "Kansas",
    capital: "Topeka",
    latitude: 39.0473,
    longitude: -95.6752,
  },
  {
    state: "Kentucky",
    capital: "Frankfort",
    latitude: 38.2009,
    longitude: -84.8733,
  },
  {
    state: "Louisiana",
    capital: "Baton Rouge",
    latitude: 30.4515,
    longitude: -91.1871,
  },
  {
    state: "Maine",
    capital: "Augusta",
    latitude: 44.3106,
    longitude: -69.7795,
  },
  {
    state: "Maryland",
    capital: "Annapolis",
    latitude: 38.9784,
    longitude: -76.4922,
  },
  {
    state: "Massachusetts",
    capital: "Boston",
    latitude: 42.3601,
    longitude: -71.0589,
  },
  {
    state: "Michigan",
    capital: "Lansing",
    latitude: 42.7325,
    longitude: -84.5555,
  },
  {
    state: "Minnesota",
    capital: "St. Paul",
    latitude: 44.9537,
    longitude: -93.09,
  },
  {
    state: "Mississippi",
    capital: "Jackson",
    latitude: 32.2988,
    longitude: -90.1848,
  },
  {
    state: "Missouri",
    capital: "Jefferson City",
    latitude: 38.5767,
    longitude: -92.1735,
  },
  {
    state: "Montana",
    capital: "Helena",
    latitude: 46.5884,
    longitude: -112.0245,
  },
  {
    state: "Nebraska",
    capital: "Lincoln",
    latitude: 40.8136,
    longitude: -96.7026,
  },
  {
    state: "Nevada",
    capital: "Carson City",
    latitude: 39.1638,
    longitude: -119.7674,
  },
  {
    state: "New Hampshire",
    capital: "Concord",
    latitude: 43.2081,
    longitude: -71.5376,
  },
  {
    state: "New Jersey",
    capital: "Trenton",
    latitude: 40.2206,
    longitude: -74.7597,
  },
  {
    state: "New Mexico",
    capital: "Santa Fe",
    latitude: 35.687,
    longitude: -105.9378,
  },
  {
    state: "New York",
    capital: "Albany",
    latitude: 42.6526,
    longitude: -73.7562,
  },
  {
    state: "North Carolina",
    capital: "Raleigh",
    latitude: 35.7796,
    longitude: -78.6382,
  },
  {
    state: "North Dakota",
    capital: "Bismarck",
    latitude: 46.8083,
    longitude: -100.7837,
  },
  {
    state: "Ohio",
    capital: "Columbus",
    latitude: 39.9612,
    longitude: -82.9988,
  },
  {
    state: "Oklahoma",
    capital: "Oklahoma City",
    latitude: 35.4676,
    longitude: -97.5164,
  },
  {
    state: "Oregon",
    capital: "Salem",
    latitude: 44.9429,
    longitude: -123.0351,
  },
  {
    state: "Pennsylvania",
    capital: "Harrisburg",
    latitude: 40.2732,
    longitude: -76.8867,
  },
  {
    state: "Rhode Island",
    capital: "Providence",
    latitude: 41.824,
    longitude: -71.4128,
  },
  {
    state: "South Carolina",
    capital: "Columbia",
    latitude: 34.0007,
    longitude: -81.0348,
  },
  {
    state: "South Dakota",
    capital: "Pierre",
    latitude: 44.3683,
    longitude: -100.351,
  },
  {
    state: "Tennessee",
    capital: "Nashville",
    latitude: 36.1627,
    longitude: -86.7816,
  },
  { state: "Texas", capital: "Austin", latitude: 30.2672, longitude: -97.7431 },
  {
    state: "Utah",
    capital: "Salt Lake City",
    latitude: 40.7608,
    longitude: -111.891,
  },
  {
    state: "Vermont",
    capital: "Montpelier",
    latitude: 44.2601,
    longitude: -72.5754,
  },
  {
    state: "Virginia",
    capital: "Richmond",
    latitude: 37.5407,
    longitude: -77.436,
  },
  {
    state: "Washington",
    capital: "Olympia",
    latitude: 47.0379,
    longitude: -122.9007,
  },
  {
    state: "West Virginia",
    capital: "Charleston",
    latitude: 38.3498,
    longitude: -81.6326,
  },
  {
    state: "Wisconsin",
    capital: "Madison",
    latitude: 43.0731,
    longitude: -89.4012,
  },
  {
    state: "Wyoming",
    capital: "Cheyenne",
    latitude: 41.14,
    longitude: -104.8202,
  },
];

export const US_STATES = stateCoordinates.map((c) => c.state);
