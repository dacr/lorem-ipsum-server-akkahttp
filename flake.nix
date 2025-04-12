{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.11";
    utils.url = "github:numtide/flake-utils";
    sbt.url = "github:zaninime/sbt-derivation";
    sbt.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = { self, nixpkgs, utils, sbt }:
  utils.lib.eachDefaultSystem (system:
  let
    pkgs = import nixpkgs { inherit system; };
  in {
    # ---------------------------------------------------------------------------
    # nix develop
    devShells.default = pkgs.mkShell {
      buildInputs = [pkgs.sbt pkgs.metals pkgs.jdk21 pkgs.hello];
    };

    # ---------------------------------------------------------------------------
    # nix build
    packages.default = sbt.mkSbtDerivation.${system} {
      pname = "nix-lorem-ipsum";
      version = builtins.elemAt (builtins.match ''[^"]+"(.*)".*'' (builtins.readFile ./version.sbt)) 0;
      depsSha256 = "sha256-AgpWO0W26gdO1+Y4GDvGszsbti9KogyPowW7vTTxnPA=";

      src = ./.;

      buildInputs = [pkgs.sbt pkgs.jdk21_headless pkgs.makeWrapper];

      buildPhase = "sbt Universal/packageZipTarball";

      installPhase = ''
          mkdir -p $out
          tar xf target/universal/lorem-ipsum-server-akkahttp.tgz --directory $out
          makeWrapper $out/bin/lorem-ipsum-server-akkahttp $out/bin/nix-lorem-ipsum \
            --set PATH ${pkgs.lib.makeBinPath [
              pkgs.gnused
              pkgs.gawk
              pkgs.coreutils
              pkgs.bash
              pkgs.jdk21_headless
            ]}
      '';
    };

    # ---------------------------------------------------------------------------
    # simple nixos services integration
    nixosModules.default = { config, pkgs, lib, ... }: {
      options = {
        services.lorem-ipsum = {
          enable = lib.mkEnableOption "lorem-ipsum";
          user = lib.mkOption {
            type = lib.types.str;
            description = "User name that will run the lorem-ipsum service";
          };
          ip = lib.mkOption {
            type = lib.types.str;
            description = "Listening network interface - 0.0.0.0 for all interfaces";
            default = "127.0.0.1";
          };
          port = lib.mkOption {
            type = lib.types.int;
            description = "Service lorem-ipsum listing port";
            default = 8080;
          };
          url = lib.mkOption {
            type = lib.types.str;
            description = "How this service is known/reached from outside";
            default = "http://127.0.0.1:8080";
          };
          prefix = lib.mkOption {
            type = lib.types.str;
            description = "Service lorem-ipsum url prefix";
            default = "";
          };
        };
      };
      config = lib.mkIf config.services.lorem-ipsum.enable {
        systemd.services.lorem-ipsum = {
          description = "Lorem ipsum service";
          environment = {
            LOREM_IPSUM_LISTEN_IP   = config.services.lorem-ipsum.ip;
            LOREM_IPSUM_LISTEN_PORT = (toString config.services.lorem-ipsum.port);
            LOREM_IPSUM_PREFIX      = config.services.lorem-ipsum.prefix;
            LOREM_IPSUM_URL         = config.services.lorem-ipsum.url;
          };
          serviceConfig = {
            ExecStart = "${self.packages.${pkgs.system}.default}/bin/nix-lorem-ipsum";
            User = config.services.lorem-ipsum.user;
            Restart = "on-failure";
          };
          wantedBy = [ "multi-user.target" ];
        };
      };
    };
    # ---------------------------------------------------------------------------

  });
}
