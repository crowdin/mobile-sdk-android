// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Crowdin Android SDK',
  tagline: 'Crowdin Android SDK delivers all new translations from Crowdin project to the application immediately',
  favicon: 'img/favicon.ico',

  url: 'https://crowdin.github.io/',
  baseUrl: '/mobile-sdk-android',
  organizationName: 'crowdin',
  projectName: 'mobile-sdk-android',

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          routeBasePath: '/',
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: 'https://github.com/crowdin/mobile-sdk-android/tree/master/website/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'Crowdin Android SDK',
        logo: {
          alt: 'Crowdin Android SDK',
          src: 'img/logo.svg',
        },
        items: [
          {
            href: 'https://github.com/crowdin/mobile-sdk-android',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Community',
            items: [
              {
                label: 'Forum',
                href: 'https://community.crowdin.com/',
              },
              {
                label: 'Twitter',
                href: 'https://twitter.com/crowdin',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/crowdin/mobile-sdk-android',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Crowdin.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ['bash', 'groovy', 'kotlin', 'java']
      },
    }),
};

module.exports = config;
