{
  description = "Generate project";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    clj-nix = {
      url = "github:jlesquembre/clj-nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
      clj-nix,
    }:
    flake-utils.lib.eachSystem [ "x86_64-linux" "aarch64-darwin" ] (
      system:
      let
        pkgs = import nixpkgs { inherit system; };
        cljpkgs = clj-nix.packages."${system}";
      in
      {
        packages = rec {
          default = native;

          jar = cljpkgs.mkCljBin {
            projectSrc = ./.;
            name = "com.github.conao3/skklsp";
            main-ns = "skklsp.core";
            jdkRunner = pkgs.jdk25_headless;
          };

          native = cljpkgs.mkGraalBin {
            cljDrv = jar;
            src = "${jar.lib}/${jar.artifactId}-${jar.version}.jar";
          };
        };

        devShells.default = pkgs.mkShell {
          buildInputs = [
            pkgs.clojure
            pkgs.openjdk
          ];
        };

        formatter = pkgs.nixfmt;
      }
    );
}
