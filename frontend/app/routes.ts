import { index, route, type RouteConfig } from "@react-router/dev/routes";

export default [
  index("routes/index.tsx"),
  route("daily", "routes/daily.tsx"),
  route("archive/:puzzleId", "routes/archive.tsx"),
] satisfies RouteConfig;
