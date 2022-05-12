import PropTypes from 'prop-types';
// material
import {Box, Link} from '@mui/material';

// ----------------------------------------------------------------------

Logo.propTypes = {
  sx: PropTypes.object
};

export default function Logo({ sx }) {
  return (
      <Box component="img" src={`${process.env.PUBLIC_URL}/static/logo.svg`} sx={{ width: 80, height: 80, ...sx }} />
  );
}
