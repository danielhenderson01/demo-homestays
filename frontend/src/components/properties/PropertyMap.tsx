import "leaflet/dist/leaflet.css";
import { icon } from "leaflet";
import { MapContainer, TileLayer, Marker, ZoomControl } from "react-leaflet";
import { getCoordinatesByState } from "@/lib/coordinates";
import { IMAGES_URL } from "@/lib/constants";

const markerIcon = icon({
  iconUrl: `${IMAGES_URL}/samples/marker-icon-2x.png`,
  iconSize: [20, 30],
});

export default function PropertyMap({
  propertyState,
}: {
  propertyState?: string;
}) {
  const stateData = getCoordinatesByState(propertyState || "Colorado");

  const coordinates = !stateData
    ? [39.7392, -104.9903]
    : [stateData.latitude, stateData.longitude];

  const defaultLocation = coordinates as [number, number];

  return (
    <MapContainer
      center={defaultLocation}
      zoom={7}
      scrollWheelZoom={false}
      zoomControl={false}
      style={{ width: "100%", height: "60vh" }}
      className="relative z-0 rounded-lg"
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <ZoomControl position="bottomright" />
      <Marker position={defaultLocation} icon={markerIcon}></Marker>
    </MapContainer>
  );
}
