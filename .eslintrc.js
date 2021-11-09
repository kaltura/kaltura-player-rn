module.exports = {
  extends: ['standard', 'prettier'],
  parser: '@typescript-eslint/parser',
  plugins: ['@typescript-eslint', 'prettier', 'react', 'react-hooks', 'detox', 'jest'],
  env: { browser: true, node: true },
  settings: {
    react: { version: 'detect' },
  },
  rules: {
    '@typescript-eslint/no-unused-vars': 2,
    '@typescript-eslint/no-unused-expressions': 2,
    'react/no-unused-prop-types': 2,
    'react/jsx-key': 2,
    'react/jsx-no-duplicate-props': 2,
    'react/jsx-no-useless-fragment': 2,
    'max-params': ['error', 3],
    'no-implicit-coercion': 2,
    'no-unused-expressions': 0,
    'no-unused-vars': 0,
    'no-undef': 0,
    'no-use-before-define': 0,
    'prettier/prettier': ['error', require('./.prettierrc')],
  },
  globals: { __DEV__: true },
}
