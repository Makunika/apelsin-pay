import * as Yup from 'yup';
import {useEffect, useState} from 'react';
import { useFormik, Form, FormikProvider } from 'formik';
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
  useEffect(() => {
    const {search} = window.location;
    const name = new URLSearchParams(search);
    if (name.has("error")) {
      enqueueSnackbar("Неправильный логин или пароль", { variant: 'error' })
    }
  }, []);

  const LoginSchema = Yup.object().shape({
    username: Yup.string().required('Логин обязателен').min(3, "Логин должен быть больше 3 символов"),
    password: Yup.string().required('Пароль обязателен')
  });

  const formik = useFormik({
    initialValues: {
      username: '',
      password: '',
    },
    validationSchema: LoginSchema,
    onSubmit: (values) => {
      const data = new FormData();
      data.append("username", values.username)
      data.append("password", values.password)
      return fetch('/perform_login', {
        method: 'post',
        mode: 'no-cors',
        cache: 'no-cache',
        body: new URLSearchParams(data)
      }).then(v => {
            if (!v.redirected) {
              return;
            }
            if (v.url.search(/^.*\?error.*/g) !== -1) {
              enqueueSnackbar("Неправильный логин или пароль", { variant: 'error' })
            } else {
              window.location = v.url
            }
          }, e => {
        console.log(e)
        enqueueSnackbar(`Ошибка: ${e.data.error}`, {variant: 'error'});
      })
    }
  })

  const { errors, touched, values, isSubmitting, handleSubmit, getFieldProps } = formik;

  const handleShowPassword = () => {
    setShowPassword((show) => !show);
  };

  return (
    <FormikProvider value={formik}>
      <Form autoComplete="off" noValidate onSubmit={handleSubmit}>
        <Stack spacing={3}>
          <TextField
            fullWidth
            autoComplete="username"
            type="text"
            label="Логин"
            {...getFieldProps('username')}
            error={Boolean(touched.username && errors.username)}
            helperText={touched.username && errors.username}
          />

          <TextField
            fullWidth
            autoComplete="current-password"
            type={showPassword ? 'text' : 'password'}
            label="Пароль"
            {...getFieldProps('password')}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton onClick={handleShowPassword} edge="end">
                    <Iconify icon={showPassword ? 'eva:eye-fill' : 'eva:eye-off-fill'} />
                  </IconButton>
                </InputAdornment>
              )
            }}
            error={Boolean(touched.password && errors.password)}
            helperText={touched.password && errors.password}
          />
        </Stack>

        <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ my: 2 }}>
          <Link variant="subtitle2" to="http://localhost:3000/reset-password" underline="hover">
            Забыли пароль?
          </Link>
        </Stack>

        <LoadingButton
          fullWidth
          size="large"
          type="submit"
          variant="contained"
          loading={isSubmitting}
        >
          Войти
        </LoadingButton>
      </Form>
    </FormikProvider>
  );
}
