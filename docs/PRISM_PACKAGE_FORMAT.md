# .prism Package Format (Draft 1)

A `.prism` file is Prisma Launcher's own mod-package container. It is not a Geode package and must not be treated as one.

## Container

Draft 1 uses a ZIP archive with the `.prism` extension. ZIP entries must use relative paths only.

Required entry:
- `manifest.json`

Optional entries:
- `README.md`
- `LICENSE`
- `assets/**`
- `payload/**` — mod payload files for a runtime/target explicitly supported by Prisma.

Example:

```text
example.prism
├── manifest.json
├── README.md
├── LICENSE
├── assets/
│   └── icon.png
└── payload/
    └── android-arm64/
        └── example.so
```

The payload layout is illustrative only. A native payload is not loadable until Prisma's independent runtime supports its ABI, game version and integration method.

## Manifest

The root `manifest.json` is UTF-8 JSON. Draft 1 fields:

| Field | Type | Required | Meaning |
|---|---|---:|---|
| `format_version` | string | yes | Package format version; currently `1` |
| `id` | string | yes | Stable reverse-DNS-like identifier |
| `name` | string | yes | Display name |
| `version` | string | yes | Mod version |
| `description` | string | yes | Short description |
| `authors` | string[] | yes | Author names or handles |
| `game` | object | yes | Supported game and version constraints |
| `runtime` | object | yes | Required Prisma runtime and target |
| `entrypoint` | string | no | Relative path to the runtime entrypoint |
| `dependencies` | object[] | no | Required mod IDs and version constraints |
| `permissions` | string[] | no | Requested capabilities |
| `assets` | object | no | Optional icon and screenshots |

### Example manifest

See [examples/minimal/manifest.json](../examples/minimal/manifest.json).

## Validation and security requirements

A future installer must:
1. Reject absolute paths, path traversal (`..`), duplicate paths and symlinks.
2. Enforce archive size, expanded size, file-count and compression-ratio limits.
3. Parse JSON strictly and reject unknown mandatory format versions.
4. Validate IDs, semantic versions, dependency constraints and target compatibility.
5. Keep packages disabled until the user explicitly enables them.
6. Never execute package contents during download or inspection.
7. Verify a trusted signature or checksum when a signed distribution flow is implemented.

A manifest's `permissions` are declarations, not proof that a permission is safe. The installer must show them to the user.

## Compatibility

Prisma must maintain its own runtime API and compatibility matrix. Do not assume that Geode or Noahh mods can run in `.prism` packages, or that renaming another package makes it compatible.

## Status

This is a draft package specification for development. It does not yet mean Prisma can install or execute mods. The archive parser, validator, package manager and independent game runtime remain implementation tasks.
