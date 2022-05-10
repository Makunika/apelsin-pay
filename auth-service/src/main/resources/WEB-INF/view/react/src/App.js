// routes
import {SnackbarProvider} from "notistack";
// theme
import ThemeConfig from './theme';
import GlobalStyles from './theme/globalStyles';
// components
import Login from "./pages/Login";

// ----------------------------------------------------------------------

export default function App() {
  return (
      <SnackbarProvider maxSnack={3}>
          <ThemeConfig>
              <GlobalStyles />
              <Login/>
          </ThemeConfig>
      </SnackbarProvider>
  );
}
