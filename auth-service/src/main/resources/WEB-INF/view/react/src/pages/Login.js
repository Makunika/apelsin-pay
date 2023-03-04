
import { styled } from '@mui/material/styles';
import {Card, Stack, Link, Container, Typography, Divider} from '@mui/material';
import AuthLayout from '../layouts/AuthLayout';
import Page from '../components/Page';
import { LoginForm } from '../sections/authentication/login';

// ----------------------------------------------------------------------

const RootStyle = styled(Page)(({ theme }) => ({
  [theme.breakpoints.up('md')]: {
    display: 'flex'
  }
}));

const SectionStyle = styled(Card)(({ theme }) => ({
  width: '100%',
  maxWidth: 464,
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  margin: theme.spacing(2, 0, 2, 2)
}));

const ContentStyle = styled('div')(({ theme }) => ({
  maxWidth: 480,
  margin: 'auto',
  display: 'flex',
  minHeight: '100vh',
  flexDirection: 'column',
  justifyContent: 'center',
  padding: theme.spacing(12, 0)
}));

// ----------------------------------------------------------------------

export default function Login() {
  return (
    <RootStyle title="Вход">
      <AuthLayout>
        Нет аккаунта? &nbsp;
        <Link variant="subtitle2" href="http://graduate.pshiblo.xyz/register" underline="none">
          Создать
        </Link>
      </AuthLayout>

      <SectionStyle sx={{ display: { xs: 'none', md: 'flex' } }}>
        <Typography variant="h3" sx={{ px: 5, mt: 10, mb: 5 }}>
          Привет, с возвращением!
        </Typography>
        <img src={`${process.env.PUBLIC_URL}/static/illustrations/illustration_login.png`} alt="login" />
      </SectionStyle>

      <Container maxWidth="sm">
        <ContentStyle>
          <Stack sx={{ mb: 5 }}>
            <Typography variant="h4" gutterBottom>
              Войти в Апельсин
            </Typography>
            <Typography gutterBottom sx={{ color: 'text.secondary' }}>Введите данные для авторизации снизу.</Typography>
            <Divider />
            <Typography color="text.secondary" variant="body1" >
              Тестовый пользователь с логином "mpshiblo3" и паролем "1234"
            </Typography>
          </Stack>

          <LoginForm />

          <Typography
            variant="body2"
            align="center"
            sx={{
              mt: 3,
              display: { sm: 'none' }
            }}
          >
            Нет аккаунта?&nbsp;
            <Link variant="subtitle2" href="http://graduate.pshiblo.xyz/register" underline="hover">
              Создать
            </Link>
          </Typography>
        </ContentStyle>
      </Container>
    </RootStyle>
  );
}
