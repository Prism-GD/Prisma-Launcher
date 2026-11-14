# .prism Validator

The first Android-side validator lives at:

`app/src/main/java/dev/prisma/launcher/packages/PrismPackageValidator.kt`

It opens a `.prism` ZIP archive in-place and validates its structure and Draft 1 manifest. It does not extract files, install a mod, grant permissions, or execute a payload.

## Checks

- File exists, has the `.prism` extension, and is within the archive size limit.
- ZIP entry count, individual uncompressed size, total uncompressed size and compression ratio are bounded.
- Absolute paths, backslashes, dot segments, parent traversal and duplicate entry names are rejected.
- `manifest.json` must exist at the archive root and be valid UTF-8 JSON.
- Draft 1 required metadata and Prisma runtime declaration are checked.
- A declared entrypoint must refer to an existing archive entry.

## API

Call `PrismPackageValidator.validate(file)`. It returns either `Result.Valid` with parsed metadata or `Result.Invalid` with a user-readable reason.

## Current limitations

This is an initial structural validator. Java's ZIP API does not expose every low-level ZIP attribute (for example Unix symlink metadata), and this implementation does not yet perform signature verification, dependency resolution, game-version matching, extraction, installation, or native loading. Those require separate implementation and tests before packages should be trusted or executed.
