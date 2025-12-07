import type { SidebarsConfig } from "@docusaurus/plugin-content-docs";

const sidebars: SidebarsConfig = {
  tutorialSidebar: [
    'intro',
    'installation',
    'setup',
    {
      type: 'category',
      label: 'Advanced Features',
      collapsible: true,
      collapsed: false,
      items: [
        'advanced-features/real-time-preview',
        'advanced-features/screenshots',
        'advanced-features/sdk-controls',
        'advanced-features/jetpack-compose',
      ]
    },
    {
      type: 'category',
      label: 'Guides',
      collapsible: true,
      collapsed: false,
      items: [
        'guides/screenshots-automation',
        'guides/multiple-flavor-app',
        'guides/platforms-support',
        'guides/synchronous-mode',
        'guides/programmatic-strings',
      ]
    },
    'example',
    'cache',
    'security',
    'faq'
  ],
};

module.exports = sidebars;
