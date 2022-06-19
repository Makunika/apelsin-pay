
import {useEffect, useState} from 'react';
// material
import {
  Link,
  Stack,
  TextField,
  IconButton,
  InputAdornment,
} from '@mui/material';
import { LoadingButton } from '@mui/lab';
// component
import {useSnackbar} from "notistack";
import Iconify from '../../../components/Iconify';

// ----------------------------------------------------------------------

export default function LoginForm(props) {
  const [showPassword, setShowPassword] = useState(false);
  const { enqueueSnackbar } = useSnackbar();
  const [loading, setLoading] = useState(false);
  useEffect(() => {
    const {search} = window.location;
    const name = new URLSearchParams(search);
    if (name.has("error")) {
      enqueueSnackbar("Неправильный логин или пароль", { variant: 'error' })
    }
  }, []);

  const handleShowPassword = () => {
    setShowPassword((show) => !show);
  };

  function handleSubmit(e) {
      setLoading(true)
  }

  return (
      <form autoComplete="off" onSubmit={handleSubmit} noValidate action={`${process.env.PUBLIC_URL}/perform_login`} method="POST">
        <Stack spacing={3}>
          <TextField
            fullWidth
            autoComplete="username"
            id="username"
            name="username"
            type="text"
            label="Логин"
          />

          <TextField
            fullWidth
            autoComplete="current-password"
            id="password"
            name="password"
            type={showPassword ? 'text' : 'password'}
            label="Пароль"
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton onClick={handleShowPassword} edge="end">
                    <Iconify icon={showPassword ? 'eva:eye-fill' : 'eva:eye-off-fill'} />
                  </IconButton>
                </InputAdornment>
              )
            }}
          />
        </Stack>

        <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ my: 2 }}>
          <Link variant="subtitle2" href="http://pshiblo.xyz/reset-password" underline="hover">
            Забыли пароль?
          </Link>
        </Stack>

        <LoadingButton
          fullWidth
          size="large"
          type="submit"
          variant="contained"
          loading={loading}
        >
          Войти
        </LoadingButton>
      </form>
  );
}
